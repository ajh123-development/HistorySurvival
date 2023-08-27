package uk.minersonline.Minecart.dc.impl.simplifier;

import uk.minersonline.Minecart.core.math.Vec3f;
import uk.minersonline.Minecart.core.math.Vec3i;
import uk.minersonline.Minecart.core.math.Vec4f;
import uk.minersonline.Minecart.core.utils.BufferUtil;
import uk.minersonline.Minecart.dc.entities.MeshBuffer;
import uk.minersonline.Minecart.dc.entities.MeshVertex;
import uk.minersonline.Minecart.dc.solver.LevenQefSolver;
import uk.minersonline.Minecart.dc.solver.QEFData;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NgMeshSimplify {
    private static final int COLLAPSE_MAX_DEGREE = 16;

    public void ngMeshSimplifier(MeshBuffer mesh, Vec3i worldSpaceOffset, MeshSimplificationOptions options) {
        if (mesh.getNumIndicates()/3 < 100 || mesh.getNumVertices() < 100) {
            return;
        }
        ArrayList<MeshVertex> vertices = copyVertices(mesh, worldSpaceOffset);
        ArrayList<MeshTriangle> triangles = copyTriangles(mesh);
        mesh.setNumVertices(0);
        mesh.setNumIndicates(0);

        ArrayList<Edge> edges = new ArrayList<>(triangles.size() * 3);
        BuildCandidateEdges(vertices, triangles, edges);

        Vec4f[] collapsePosition = new Vec4f[edges.size()];
        Vec3f[] collapseNormal = new Vec3f[edges.size()];
        ArrayList<Integer> collapseValid = new ArrayList<>(edges.size());
        int[] collapseEdgeID = new int[vertices.size()];
        int[] collapseTarget = new int[vertices.size()];

        ArrayList<Edge> edgeBuffer = new ArrayList<>(edges.size());
        ArrayList<MeshTriangle> triBuffer = new ArrayList<>(triangles.size());

        // per vertex
        int[] vertexTriangleCounts = new int[vertices.size()];
        for (MeshTriangle triangle : triangles) {
            for (int index = 0; index < 3; index++) {
                vertexTriangleCounts[triangle.indices[index]] += 1;
            }
        }

        int targetTriangleCount = (int) (triangles.size() * options.targetPercentage);
        int iterations = 0;

        while (triangles.size() > targetTriangleCount &&
                iterations++ < options.maxIterations) {
            Arrays.fill(collapseEdgeID, -1);
            Arrays.fill(collapseTarget, -1);

            collapseValid.clear();
		    int countValidCollapse = FindValidCollapses(
                options,
                edges, vertices, triangles, vertexTriangleCounts, collapseValid,
                collapseEdgeID, collapsePosition, collapseNormal);
            if (countValidCollapse == 0) {
                break;
            }

            CollapseEdges(collapseValid, edges,
                    collapseEdgeID, collapsePosition, collapseNormal, vertices,
                    collapseTarget);

            RemoveTriangles(vertices, collapseTarget, triangles, triBuffer, vertexTriangleCounts);
            RemoveEdges(collapseTarget, edges, edgeBuffer);
        }

        mesh.setNumIndicates(0);
        for (int i = 0; i < triangles.size(); i++) {
            int index = i * 3;
            mesh.getIndicates().put(index + 0, triangles.get(i).indices[0]);
            mesh.getIndicates().put(index + 1, triangles.get(i).indices[1]);
            mesh.getIndicates().put(index + 2, triangles.get(i).indices[2]);
        }
        mesh.setNumIndicates(triangles.size()*3);
        CompactVertices(vertices, mesh);

        mesh.setNumVertices(vertices.size());
        mesh.setVertices(BufferUtil.createDcFlippedBufferAOS(vertices, worldSpaceOffset));
    }

    static void BuildCandidateEdges(ArrayList<MeshVertex> vertices, ArrayList<MeshTriangle> triangles, ArrayList<Edge> edges) {
        for (MeshTriangle triangle : triangles) {
            int[] indices = triangle.indices;
            edges.add(new Edge(min(indices[0], indices[1]), max(indices[0], indices[1])));
            edges.add(new Edge(min(indices[1], indices[2]), max(indices[1], indices[2])));
            edges.add(new Edge(min(indices[0], indices[2]), max(indices[0], indices[2])));
        }
        edges.sort(Comparator.comparingLong(Edge::getIdx));

        ArrayList<Edge> filteredEdges = new ArrayList<>(edges.size());
        boolean[] boundaryVerts = new boolean[vertices.size()];

        Edge prev = edges.get(0);
        int count = 1;
        for (int idx = 1; idx < edges.size(); idx++) {
            Edge curr = edges.get(idx);
            if (curr.getIdx() != prev.getIdx()) {
                if (count == 1) {
                    boundaryVerts[prev.getMin()] = true;
                    boundaryVerts[prev.getMax()] = true;
                }
                else {
                    filteredEdges.add(prev);
                }
                count = 1;
            }
            else {
                count++;
            }
            prev = curr;
        }
        edges.clear();
        for (Edge edge: filteredEdges) {
            if (!boundaryVerts[edge.getMin()] && !boundaryVerts[edge.getMax()]){
                edges.add(edge);
            }
        }
    }

    static int FindValidCollapses(
            MeshSimplificationOptions options,
            ArrayList<Edge> edges,
            ArrayList<MeshVertex> vertices,
            ArrayList<MeshTriangle> tris,
            int[] vertexTriangleCounts,
            ArrayList<Integer> collapseValid,
            int[] collapseEdgeID,
            Vec4f[] collapsePosition,
            Vec3f[] collapseNormal)
    {
        int validCollapses = 0;
        int numRandomEdges = (int) (edges.size() * options.edgeFraction);
        Random r = new Random(42);
        ArrayList<Integer> randomEdges = new ArrayList<>(numRandomEdges);
        for (int i = 0; i < numRandomEdges; i++) {
            int randomIdx = randomGet(r, 0, (edges.size() - 1));
            randomEdges.add(randomIdx);
        }
        Collections.sort(randomEdges);

        float[] minEdgeCost = new float[vertices.size()];
        Arrays.fill(minEdgeCost, Float.MAX_VALUE);

        for (int i: randomEdges) {
            Edge edge = edges.get(i);
            MeshVertex vMin = vertices.get(edge.getMin());
            MeshVertex vMax = vertices.get(edge.getMax());

            // prevent collapses along edges
            float cosAngle = vMin.getNormal().dot(vMax.getNormal());
            if (cosAngle < options.minAngleCosine) {
                continue;
            }

            float edgeSize = vMax.getPos().sub(vMin.getPos()).lengthSquared();
            if (edgeSize > (options.maxEdgeSize * options.maxEdgeSize)) {
                continue;
            }

            if (Math.abs(vMin.getColor().Z - vMax.getColor().getZ()) > 1e-3) { // [ToDo] color.w ???
                continue;
            }

            int degree = vertexTriangleCounts[edge.getMin()] + vertexTriangleCounts[edge.getMax()];
            if (degree > COLLAPSE_MAX_DEGREE) {
                continue;
            }

            Vec4f[] positions = {new Vec4f(vMin.getPos()), new Vec4f(vMax.getPos())};
            Vec4f[] normals = {new Vec4f(vMin.getNormal()), new Vec4f(vMax.getNormal())};

            QEFData qef = new QEFData(new LevenQefSolver());
            qef.qef_create_from_points(positions, normals, 2);
            Vec4f solvedPos = qef.solve();
            float error = qef.getError();
            if (error > 0.f) {
                error = 1.f / error;
            }

            // avoid vertices becoming a 'hub' for lots of edges by penalising collapses which will lead to a vertex with degree > 10
            int penalty = Math.max(0, degree - 10);
            error += penalty * (options.maxError * 0.1f);
            if (error > options.maxError) {
                continue;
            }

            collapseValid.add(i);

            collapseNormal[i] = vMin.getNormal().add(vMax.getNormal()).mul(0.5f);//sub !!!
            collapsePosition[i] = new Vec4f(solvedPos.x, solvedPos.y, solvedPos.z, 1.f);

            if (error < minEdgeCost[edge.getMin()]){
                minEdgeCost[edge.getMin()] = error;
                collapseEdgeID[edge.getMin()] = i;
            }

            if (error < minEdgeCost[edge.getMax()]){
                minEdgeCost[edge.getMax()] = error;
                collapseEdgeID[edge.getMax()] = i;
            }
            validCollapses++;
        }
        return validCollapses;
    }

    static void CollapseEdges(
            ArrayList<Integer> collapseValid,
            ArrayList<Edge> edges,
            int[] collapseEdgeID,
            Vec4f[] collapsePositions,
            Vec3f[] collapseNormal,
            ArrayList<MeshVertex> vertices,
            int[] collapseTarget)
    {
        for (int i: collapseValid) {
            Edge edge = edges.get(i);
            if (collapseEdgeID[edge.getMin()] == i && collapseEdgeID[edge.getMax()] == i){
                collapseTarget[edge.getMax()] = edge.getMin();
                vertices.get(edge.getMin()).getPos().set(collapsePositions[i].x, collapsePositions[i].y, collapsePositions[i].z);
                vertices.get(edge.getMin()).setNormal(collapseNormal[i]);
            }
        }
    }

    private static int RemoveTriangles(ArrayList<MeshVertex> vertices,
                                       int[] collapseTarget,
                                       ArrayList<MeshTriangle> tris,
                                       ArrayList<MeshTriangle> triBuffer,
                                       int[] vertexTriangleCounts) {
        int removedCount = 0;
        Arrays.fill(vertexTriangleCounts, 0);
        triBuffer.clear();

        for (MeshTriangle tri: tris) {
            for (int j = 0; j < 3; j++) {
                int t = collapseTarget[tri.indices[j]];
                if (t != -1) {
                    tri.indices[j] = t;
                }
            }
            if (tri.indices[0] == tri.indices[1] ||
                    tri.indices[0] == tri.indices[2] ||
                    tri.indices[1] == tri.indices[2]) {
                removedCount++;
                continue;
            }
		    int[] indices = tri.indices;
            for (int index = 0; index < 3; index++) {
                vertexTriangleCounts[indices[index]] += 1;
            }
            triBuffer.add(tri);
        }
        swap(tris, triBuffer);
        return removedCount;
    }

    private static void RemoveEdges(int[] collapseTarget, ArrayList<Edge> edges, ArrayList<Edge> edgeBuffer) {
        edgeBuffer.clear();
        for (Edge edge: edges) {
            int t = collapseTarget[edge.getMin()];
            if (t != -1) {
                edge.setMin(t);
            }

            t = collapseTarget[edge.getMax()];
            if (t != -1) {
                edge.setMax(t);
            }

            if (edge.getMin() != edge.getMax())
            {
                edgeBuffer.add(edge);
            }
        }
        swap(edges, edgeBuffer);
    }

    static void CompactVertices(ArrayList<MeshVertex> vertices, MeshBuffer meshBuffer) {
        boolean[] vertexUsed = new boolean[vertices.size()];

//        for (int i = 0; i < meshBuffer->numTriangles; i++) {
//            MeshTriangle tri = meshBuffer->triangles[i];
//            vertexUsed.set(tri.indices[0], true);
//            vertexUsed.set(tri.indices[1], true);
//            vertexUsed.set(tri.indices[2], true);
//        }

        for (int i = 0; i < meshBuffer.getNumIndicates(); i++) {
            int tri = meshBuffer.getIndicates().get(i);
            vertexUsed[tri]= true;
        }

        ArrayList<MeshVertex> compactVertices = new ArrayList<>(vertices.size());
        int[] remappedVertexIndices = new int [vertices.size()];
        Arrays.fill(remappedVertexIndices, -1);

        for (int i = 0; i < vertices.size(); i++) {
            if (vertexUsed[i]) {
                remappedVertexIndices[i] = compactVertices.size();
                compactVertices.add(vertices.get(i));
            }
        }

//        for (int i = 0; i < meshBuffer->numTriangles; i++) {
//            MeshTriangle tri = meshBuffer.triangles[i];
//            for (int j = 0; j < 3; j++) {
//                tri.indices[j] = remappedVertexIndices.get(tri.indices[j]);
//            }
//        }

        for (int i = 0; i < meshBuffer.getNumIndicates(); i++) {
            meshBuffer.getIndicates().put(i, remappedVertexIndices[meshBuffer.getIndicates().get(i)]);
        }
        swap(vertices, compactVertices);
    }

    public static <T>void swap(List<T> list1, List<T> list2){
        List<T> tmpList = new ArrayList<>(list1);
        list1.clear();
        list1.addAll(list2);
        list2.clear();
        list2.addAll(tmpList);
    }

    public static int randomGet(Random rnd, int min, int max){// get random number from min to max (not max-1 !)
        return min + (int) Math.floor(rnd.nextDouble() * (max - min + 1));
    }

    private ArrayList<MeshTriangle> copyTriangles(MeshBuffer mesh){
        ArrayList<MeshTriangle> triangles = new ArrayList<>(mesh.getNumIndicates()/3);
        for (int i = 0; i < mesh.getNumIndicates()/3; i++) {
            int index = i * 3;
            int i1 = mesh.getIndicates().get(index + 0);
            int i2 = mesh.getIndicates().get(index + 1);
            int i3 = mesh.getIndicates().get(index + 2);
            MeshTriangle meshTriangle = new MeshTriangle(i1, i2, i3);
            triangles.add(meshTriangle);
        }
        return triangles;
    }

    private ArrayList<MeshVertex> copyVertices(MeshBuffer mesh, Vec3i worldSpaceOffset){
        ArrayList<MeshVertex> vertices = new ArrayList<>(mesh.getNumVertices());
        for (int i = 0; i < mesh.getNumVertices(); i++) {
            int index = i * 9;
            float x = mesh.getVertices().get(index + 0);
            float y = mesh.getVertices().get(index + 1);
            float z = mesh.getVertices().get(index + 2);
            float normX = mesh.getVertices().get(index + 3);
            float normY = mesh.getVertices().get(index + 4);
            float normZ = mesh.getVertices().get(index + 5);
            float colorX = mesh.getVertices().get(index + 6);
            float colorY = mesh.getVertices().get(index + 7);
            float colorZ = mesh.getVertices().get(index + 8);

            x -= worldSpaceOffset.x;
            y -= worldSpaceOffset.y;
            z -= worldSpaceOffset.z;

            MeshVertex meshVertex = new MeshVertex();
            meshVertex.setPos(new Vec3f(x, y, z));
            meshVertex.setNormal(new Vec3f(normX, normY, normZ));
            meshVertex.setColor(new Vec3f(colorX, colorY, colorZ));
            vertices.add(meshVertex);
        }
        return vertices;
    }
}
