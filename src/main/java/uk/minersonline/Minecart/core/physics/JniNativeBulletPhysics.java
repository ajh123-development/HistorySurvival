package uk.minersonline.Minecart.core.physics;


//import com.jme3.bullet.PhysicsSpace;
//import com.jme3.bullet.SolverType;
//import com.jme3.bullet.collision.PhysicsCollisionObject;
//import com.jme3.bullet.collision.PhysicsRayTestResult;
//import com.jme3.bullet.collision.shapes.MeshCollisionShape;
//import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
//import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
//import com.jme3.bullet.objects.PhysicsRigidBody;
//import com.jme3.math.Vector3f;
//import com.jme3.system.NativeLibraryLoader;

import uk.minersonline.Minecart.core.math.Vec3f;
import uk.minersonline.Minecart.terrain.ChunkNode;
import uk.minersonline.Minecart.terrain.utils.Aabb;

public class JniNativeBulletPhysics implements Physics {
    private Vec3f collisionPos = new Vec3f();;
    private Vec3f collisionNorm = new Vec3f();;
//    final float PHYSICS_SCALE = 1f;
//    private PhysicsSpace dynamicsWorld;
//    private Aabb g_worldBounds;
//    private boolean g_physicsQuit = false;
//    private ConcurrentLinkedQueue<Runnable> g_operationQueue;
//    private Thread g_physicsThread;
//    private ExecutorService service;
//    private volatile boolean g_rayCastPending = false;

    @Override
    public void Physics_TogglePlayerNoClip() {
    }

    @Override
    public void Physics_PlayerJump() {
    }

    @Override
    public Vec3f getCollisionNorm() {
        return collisionNorm;
    }

    @Override
    public Vec3f getCollisionPos() {
        return collisionPos;
    }

    @Override
    public void Physics_SetPlayerVelocity(Vec3f velocity) {
    }

    @Override
    public Vec3f Physics_GetPlayerPosition() {
        return new Vec3f();
    }

    @Override
    public void Physics_SpawnPlayer(Vec3f origin) {
    }

    @Override
    public int getMaxChunkSize() {
        return 0;
    }

    @Override
    public void Physics_UpdateWorldNodeMainMesh(boolean updateMain, ChunkNode chunkNode) {
//        if((updateMain && chunkNode.renderMesh==null) || (!updateMain && chunkNode.seamMesh==null)){
//            return;
//        }
//        MeshBuffer meshBuffer = updateMain ? chunkNode.renderMesh.meshBuffer : chunkNode.seamMesh.meshBuffer;
//        EnqueuePhysicsOperation(PhysicsOp_WorldUpdate, ()->UpdateCollisionNode(updateMain, chunkNode.worldNode, chunkNode.min, meshBuffer));
    }

    @Override
    public void RemoveMeshData(PhysicsMeshData meshData) {
//        dynamicsWorld.removeCollisionObject(meshData.nativeBody);
//        meshData.body = null;
//        meshData.shape = null;
//        meshData.buffer = null;
    }

    @Override
    public void Physics_CastRay(Vec3f start, Vec3f end) {
//        if (!g_rayCastPending) {
//            g_rayCastPending = true;
//            EnqueuePhysicsOperation(PhysicsOp_RayCast, () -> CastRayImpl(start, end));
//        }
    }

    @Override
    public void Physics_Shutdown() {
//        g_operationQueue.clear();
//        g_physicsQuit = true;
//        try {
//            g_physicsThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public JniNativeBulletPhysics(Aabb g_worldBounds) {
//        service = Executors.newFixedThreadPool(1);
//        g_operationQueue = new ConcurrentLinkedQueue<>();
//        Physics_Initialise(g_worldBounds);
    }

    /*
    private void EnqueuePhysicsOperation(PhysicsOperationType opType, Runnable op) {
        try {
            boolean res = g_operationQueue.add(op);
            if(!res){
                int t=3;
            }
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    private Vec3f Scale_WorldToPhysics(Vec3f worldValue) {
        return new Vec3f(worldValue.X * PHYSICS_SCALE, worldValue.Y * PHYSICS_SCALE, worldValue.Z * PHYSICS_SCALE);
    }

    private void Physics_Initialise(Aabb worldBounds) {
        String homePath = System.getProperty("user.home");
        File downloadDirectory = new File(homePath, "Downloads");
        NativeLibraryLoader.loadLibbulletjme(true, downloadDirectory, "Release", "Sp");

        g_worldBounds = worldBounds;
        Vector3f worldMin = new Vector3f(g_worldBounds.min.x, g_worldBounds.min.y, g_worldBounds.min.z);
        Vector3f worldMax = new Vector3f(g_worldBounds.max.x, g_worldBounds.max.y, g_worldBounds.max.z);
        dynamicsWorld = new PhysicsSpace(worldMin, worldMax, PhysicsSpace.BroadphaseType.AXIS_SWEEP_3);
        dynamicsWorld.setGravity(new Vector3f(0, -9.8f, 0));
        dynamicsWorld.getSolverInfo().setNumIterations(5);

        g_physicsQuit = false;
        g_physicsThread = new Thread(this::PhysicsThreadFunction);
        g_physicsThread.start();
    }

    // call after node is update (CSG operation)
    private void UpdateCollisionNode(boolean updateMain, WorldCollisionNode node, Vec3i min, MeshBuffer meshBuffer) {
        service.submit(() -> {
            if (meshBuffer!=null) {
                try{
                    PhysicsMeshData newMesh = addMeshToWorldImpl(min, meshBuffer);
                    EnqueuePhysicsOperation(PhysicsOp_WorldUpdate, ()->ReplaceCollisionNodeMesh(updateMain, node, newMesh));
                } catch (Throwable e){
                    e.printStackTrace();
                }
            }
        });
    }

    private Vector3f[] getPositionArray(MeshBuffer buf){
        Vector3f[] array = new Vector3f[buf.getNumVertices()];
        for (int i = 0; i < buf.getNumVertices(); i++) {
            int index = i * 9;
            Vector3f pos = new Vector3f();
            pos.x = buf.getVertices().get(index + 0);
            pos.y = buf.getVertices().get(index + 1);
            pos.z = buf.getVertices().get(index + 2);
            array[i] = pos;
        }
        return array;
    }

    private int[] getIndexArray(MeshBuffer buf){
        int[] arr = new int[buf.getNumIndicates()];
        for (int i=0; i<buf.getNumIndicates(); i++){
            arr[i] = buf.getIndicates().get(i);
        }
        //buf.getIndicates().flip();
        return arr;
    }

    private PhysicsMeshData addMeshToWorldImpl(Vec3i nodeMin, MeshBuffer meshBuffer){
        IndexedMesh indexedMesh = new IndexedMesh(getPositionArray(meshBuffer), getIndexArray(meshBuffer));

        PhysicsMeshData meshData = new PhysicsMeshData();
        meshData.nativeBuffer = new CompoundMesh();
        meshData.nativeBuffer.add(indexedMesh);
        meshData.nativeShape = new MeshCollisionShape(true, indexedMesh);
        meshData.nativeBody = new PhysicsRigidBody(meshData.nativeShape);
        meshData.nativeBody.setFriction(0.9f);
        return meshData;
    }

    private void ReplaceCollisionNodeMesh(boolean replaceMainMesh, WorldCollisionNode node, PhysicsMeshData newMesh) {
        PhysicsMeshData oldMesh;
        if (replaceMainMesh) {
            oldMesh = node.mainMesh;
            node.mainMesh = newMesh;
        }
        else {
            oldMesh = node.seamMesh;
            node.seamMesh = newMesh;
        }

        if (newMesh!=null) {
            dynamicsWorld.addCollisionObject(newMesh.nativeBody);
        }
        if (oldMesh!=null) {
            RemoveMeshData(oldMesh);
        }
    }

    private void CastRayImpl(Vec3f start, Vec3f end){
        Vector3f rayStart = new Vector3f(start.X, start.Y, start.Z);
        Vector3f rayEnd = new Vector3f(end.X, end.Y, end.Z);

        if(dynamicsWorld.countCollisionObjects()>0){
            int t=3;
        }
        List<PhysicsRayTestResult> rayTest = dynamicsWorld.rayTest(rayStart, rayEnd);
        if (rayTest.size() > 0) {
            PhysicsRayTestResult nearestHit = rayTest.get(0);
            PhysicsCollisionObject pco = nearestHit.getCollisionObject();
            Object user = pco.getUserObject();
            int v = 7;
        }
        g_rayCastPending = false;
    }

    private void PhysicsThreadFunction() {
        long prevTime = System.nanoTime();
        while (!g_physicsQuit) {
            Runnable task;
            while (g_operationQueue.size() > 0) {
                try {
                    if ((task = g_operationQueue.poll()) != null) {
                        task.run();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

		    long deltaTime = System.nanoTime() - prevTime;
		    float dt = deltaTime / 1000.f;
		    float updatePeriod = 1 / 60.f;
            if (dt < updatePeriod) {
                continue;
            }
            prevTime = System.nanoTime();
		    dynamicsWorld.update(dt);
        }
    }
     */
}