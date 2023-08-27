package tk.minersonline.Minecart.util;

public class ArrayUtils {
	public static int[] addAll(int[] original, int[] input) {
		int originalLength = original.length;
		int inputLength = input.length;

		// Create a new array to store the combined elements
		int[] result = new int[originalLength + inputLength];

		// Copy elements from the original array to the result array
		System.arraycopy(original, 0, result, 0, originalLength);

		// Copy elements from the input array to the result array
		System.arraycopy(input, 0, result, originalLength, inputLength);

		return result;
	}

	public static int[] addAll(int[] original, int i, int i1, int i2, int i3, int i4, int i5) {
		int originalLength = original.length;

		// Create a new array to store the combined elements
		int[] result = new int[originalLength + 5];

		// Copy elements from the original array to the result array
		System.arraycopy(original, 0, result, 0, originalLength);

		// Add the individual elements to the result array
		result[originalLength] = i;
		result[originalLength + 1] = i1;
		result[originalLength + 2] = i2;
		result[originalLength + 3] = i3;
		result[originalLength + 4] = i4;
		result[originalLength + 5] = i5;

		return result;
	}
}
