package uk.minersonline.Minecart.terrain.impl.simplifier;

public class MeshSimplificationOptions {
    // Each iteration involves selecting a fraction of the edges at random as possible
    // candidates for collapsing. There is likely a sweet spot here trading off against number
    // of edges processed vs number of invalid collapses generated due to collisions
    // (the more edges that are processed the higher the chance of collisions happening)
    float edgeFraction = 1f;

    // Stop simplfying after a given number of iterations
    int maxIterations = 10;

    // And/or stop simplifying when we've reached a percentage of the input triangles
    float targetPercentage = 0.05f;

    // The maximum allowed error when collapsing an edge (error is calculated as 1.0/qef_error)
    public float maxError = 5.f;

    // Useful for controlling how uniform the mesh is (or isn't)
    public float maxEdgeSize = 2.5f;

    // If the mesh has sharp edges this can used to prevent collapses which would otherwise be used
    public float minAngleCosine = 0.8f;
}
