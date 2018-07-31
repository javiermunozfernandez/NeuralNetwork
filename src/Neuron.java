import java.util.Arrays;
import java.util.Random;

public class Neuron {
	private final Random RNG = new Random(1);
	private double[] weights;
	private double currentDerivative = 0;
	private double lastOutput = 0;
	private Neuron[] inputs;
	private int id;
	private static int num = 0;
	public Neuron(final Neuron[] inputNeurons) {
		weights = new double[inputNeurons.length];
		inputs = new Neuron[inputNeurons.length];
		for (int i = 0; i < inputNeurons.length; i++) {
			inputs[i] = inputNeurons[i];
			weights[i] = RNG.nextDouble() + RNG.nextDouble() - 1;
		}
		id = num++;
	}
	public Neuron(final double input) {
		weights = null;
		inputs = null;
		lastOutput = input;
		id = num++;
	}
	public static double sigmoid(final double x) {
		return 1.0/(1.0 + Math.exp(-x));
	}
	public void modifyWeights(double error) {
		if (inputs != null) {
			final double delta = error * currentDerivative;
			for (int i = 0; i < weights.length; i++) {
				inputs[i].modifyWeights(delta * weights[i]);
				weights[i] += delta * inputs[i].getOutput();
			} 
		}
	}
	public void setOutput(final double out) {
		lastOutput = out;
	}
	public double getOutput() {
		return lastOutput;
	}
	public double think() {
		if (inputs != null) {
			double sum = 0;
			for (int i = 0; i < inputs.length; i++) {
				sum += inputs[i].think() * weights[i];
			}
			lastOutput = sigmoid(sum);
			currentDerivative = lastOutput*(1.0 - lastOutput);
		}
		return lastOutput;
	}
	public String toString() {
		return id + ": " + Arrays.toString(inputs);
	}
	public String weightInfo() {
		String info = "";
		if (weights != null) {
			for (double weight : weights) {
				info = info + weight + " ";
			}
		}
		return info + "\n";
	}
	public double[] getWeights() {
		return weights;
	}
}
