import org.joml.Vector3f;
import tk.minersonline.Minecart.templates.models.Cube;

public class CubeMeshTest {
	public static void main(String[] args) {
		int count = 0;
		for (Vector3f[] vector3fs : Cube.FACE_VERTICES) {
			for (Vector3f vector3f : vector3fs) {
				System.out.println(vector3f.x + ", " + vector3f.y + ", " + vector3f.z);
				count = count + 1;
			}
		}
		System.out.println("Total count "+count);
	}
}
