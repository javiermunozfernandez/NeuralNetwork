import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Network {
	final int hiddenUnits;
	final double learningRate;
	final Neuron[] inputLayer;
	final Neuron[] outputLayer;
	final Neuron[][] hiddenLayers;
	public Network(final int units, final double rate,
				   final int numInputs, final int numOutputs, final int layers) {
		hiddenUnits = units;
		hiddenLayers = new Neuron[layers][units];
		learningRate = rate;
		inputLayer = new Neuron[numInputs + 1];
		outputLayer = new Neuron[numOutputs];
		initializeNeuronLayers();
	}
	private void initializeNeuronLayers() {
		//Input Layer
		inputLayer[0] = new Neuron(1); //bias
		for (int i = 1; i < inputLayer.length; i++) {
			inputLayer[i] = new Neuron(0);
		}
		//First Hidden Layer
		hiddenLayers[0] = new Neuron[hiddenUnits + 1];
		hiddenLayers[0][0] = new Neuron(1); //bias
		for (int i = 1; i < hiddenLayers[0].length; i++) {
			hiddenLayers[0][i] = new Neuron(inputLayer);
		}
		//Other Hidden Layers
		for (int i = 1; i < hiddenLayers.length; i++) {
			hiddenLayers[i] = new Neuron[hiddenUnits + 1];
			hiddenLayers[i][0] = new Neuron(1); //bias
			for (int j = 1; j < hiddenLayers[i].length; j++) {
				hiddenLayers[i][j] = new Neuron(hiddenLayers[i - 1]);
			}
		}
		//Output Layer
		for (int i = 0; i < outputLayer.length; i++) {
			outputLayer[i] = new Neuron(hiddenLayers[hiddenLayers.length - 1]);
		}
	}
	public Network() {
		this(3);
	}
	public Network(final int inputs) {
		this(inputs, 1);
	}
	public Network(final int inputs, final int outputs) {
		this(5, 1, inputs, outputs, 2);
	}
	public double[] think(final double[] input) {
		for (int i = 0; i < input.length; i++) {
			inputLayer[i + 1].setOutput(input[i]);
		}
		final double[] result = new double[outputLayer.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = outputLayer[i].think();
		}
		return result;
	}
	public void train(final double[][] in, final double[][] out,
					  final int numTimes) {
		System.out.println(numTimes + " Training Session(s) Later");
		double[][] errors = null;
		for (int i = 0; i < numTimes; i++) {
			errors = trainOnce(in, out);
		}
		double totalError = 0;
		for (int output = 0; output < errors[0].length; output++) {
			double outputError = 0;
			for (int trial = 0; trial < errors.length; trial++) {
				outputError += errors[trial][output] * errors[trial][output];
			}
			outputError = outputError / errors.length;
			totalError += outputError;
			System.out.println("Output " + output + " Error: " + outputError);
		}
		System.out.println("Total Error: " + totalError);
		System.out.println("Average Error: " + totalError / errors[0].length);
	}
	private double[][] trainOnce(final double[][] inputs, final double[][] outputs) {
		final double[][] errors = new double[outputs.length][outputs[0].length];
		for (int i = 0; i < inputs.length; i++) {
			final double[] networkResult = this.think(inputs[i]);
			final double[] realResult = outputs[i];
			for (int k = 0; k < outputLayer.length; k++) {
				final double error = realResult[k] - networkResult[k];
				errors[i][k] = error;
				outputLayer[k].modifyWeights(error * learningRate);
			}
		}
		return errors; 
	}
	public void save(final String file) throws IOException {
		FileWriter outputStream = new FileWriter(file);
		outputStream.write(	hiddenUnits + " "+ 
							learningRate + " " +
							(inputLayer.length - 1) + " " +
							outputLayer.length + " " +
							hiddenLayers.length + "\n");
		for (final Neuron[] layer : hiddenLayers) {
			for (final Neuron n: layer) {
				outputStream.write(n.weightInfo());
			}
		}
		outputStream.write("\n");
		for (final Neuron n: outputLayer) {
			outputStream.write(n.weightInfo());
		}
		outputStream.write("\n");
		outputStream.close();
	}
	public static Network load(final String file) throws FileNotFoundException {
		Scanner in = new Scanner(new File(file));
		final int units, numInputs, numOutputs, layers;
		final double rate;
		units = in.nextInt();
		rate = in.nextDouble();
		numInputs = in.nextInt();
		numOutputs = in.nextInt();
		layers = in.nextInt();
		final Network net = new Network(units, rate, numInputs, numOutputs, layers);
		for (int i = 0; i < layers; i ++) {
			for (int j = 0; j < units + 1; j++) {
				final double[] weights = net.hiddenLayers[i][j].getWeights();
				if (weights != null) {
					for (int k = 0; k <  weights.length; k++) {
						weights[k] = in.nextDouble();
					}
				}
			}
		}
		for (int i = 0; i < numOutputs; i++) {
			final double[] weights = net.outputLayer[i].getWeights();
			for (int k = 0; k < weights.length; k ++) {
				weights[k] = in.nextDouble();
			}
		}
		in.close();
		return net;
	}
}
