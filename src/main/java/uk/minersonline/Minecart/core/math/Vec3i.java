package uk.minersonline.Minecart.core.math;

public class Vec3i {
    public int x;
    public int y;
    public int z;

    public Vec3i(){ }

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(int r) {
        this.x = r;
        this.y = r;
        this.z = r;
    }

    public Vec3i(float x, float y, float z) {
        this.x = (int)x;
        this.y = (int)y;
        this.z = (int)z;
    }

    public Vec3f toVec3f(){
        return new Vec3f(this.x, this.y, this.z);
    }

    public Vec4f toVec4f(){
        return new Vec4f(this.x, this.y, this.z);
    }

    public Vec3i sub(Vec3i r)
    {
        return new Vec3i(this.x - r.x, this.y - r.y, this.z - r.z);
    }

    public Vec3i sub(int r)
    {
        return new Vec3i(this.x - r, this.y - r, this.z - r);
    }

    public Vec3i add(Vec3i r)
    {
        return new Vec3i(this.x + r.x, this.y + r.y, this.z + r.z);
    }

    public void add(int X, int Y, int Z)
    {
        this.x += X;
        this.y += Y;
        this.z += Z;
    }

    public Vec3i add(Vec3f r)
    {
        return new Vec3i(this.x + (int)r.X, this.y + (int)r.Y, this.z + (int)r.Z);
    }

    public Vec3i add(int r)
    {
        return new Vec3i(this.x + r, this.y + r, this.z + r);
    }

    public Vec3i mul(Vec3i r)
    {
        return new Vec3i(this.x * r.x, this.y * r.y, this.z * r.z);
    }

    public Vec3i mul(int x, int y, int z)
    {
        return new Vec3i(this.x * x, this.y * y, this.z * z);
    }

    public Vec3i mul(int r)
    {
        return new Vec3i(this.x * r, this.y * r, this.z * r);
    }

    public Vec3i div(int r)
    {
        return new Vec3i(this.x / r, this.y / r, this.z / r);
    }

    public void set(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vec3i that)
    {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3i vec3i = (Vec3i) o;
        return x == vec3i.x &&
                y == vec3i.y &&
                z == vec3i.z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    public String toString()
    {
        return "[" + this.x + "," + this.y + "," + this.z + "]";
    }

    public int[] to1dArray(){
        return new int[] {x, y, z};
    }

    private int sign(int x){
        return Integer.compare(x, 0);
    }

    Vec3i snapPositionToBrushCentre(Vec3i position, Vec3i brushSize) {
        Vec3i halfBrushSize = brushSize.div(2);

        // round the position toward the next half size position, when in quadrants with -ve values
        // we need to snap in the opposite direction, so control the direction by multiplying by sign()
        return new Vec3i(
                position.x + (sign(position.x) * halfBrushSize.x - (position.x % brushSize.x)),
                position.y + (sign(position.y) * halfBrushSize.y - (position.y % brushSize.y)),
                position.z + (sign(position.z) * halfBrushSize.z - (position.z % brushSize.z)));
    }
}
