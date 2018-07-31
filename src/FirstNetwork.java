import java.io.IOException;

public class FirstNetwork {
	public static void main(final String args[]) throws IOException {
		final int numInputs = 5;
		final int numOutputs = 5;
		final int possibilities = (int) Math.pow(2, numInputs);
		final double[][] inputs = new double[possibilities][numInputs];
		final double[][] output = new double[possibilities][numOutputs];
		int x = 0;
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = toBinaryArray(x++, inputs[i].length);
			for (int j = 0; j < output[i].length; j++) {
				if ((x + j) % numOutputs == 0) {
					output[i][j] = 1;
				} else {
					output[i][j] = 0;
				}
			}
		} 
		Network net = new Network(inputs[0].length, output[0].length);
		net.train(inputs, output, 1000);
		net.save("net.txt");
		Network net1 = Network.load("net.txt");
		net1.save("net1.txt");
	}
	public static double[] toBinaryArray(final int num, final int size) {
		final double[] bin = new double[size];
	    for (int i = 0; i < bin.length; i++) {
	    	if ((1 << i & num) != 0) {
	    		bin[bin.length - 1 - i] = 1;
	    	} else {
	    		bin[bin.length - 1 - i] = 0;
	    	}
	    }
	    return bin;
	}
}

