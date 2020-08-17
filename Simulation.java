import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.lang.*;

public class Simulation{
	public static void main(String []args) {
		Simulation simulation = new Simulation();
		// DataSet.
		try {
			boolean created = false;
			File dataSetDirectory = new File("dataSet");
			if(dataSetDirectory.exists()) {
				Scanner choosingNewOrExistScanner = new Scanner(System.in);
				System.out.println("Here exists a dataSet, whether create a new one? (Yes/No).");
				String choosing = choosingNewOrExistScanner.nextLine();
				if(choosing.contentEquals("No")||choosing.contentEquals("N")) {
					// Use old dataSet.
					// Get the dataSetSize.
					File list[] = dataSetDirectory.listFiles();
					int counter = 0;
					for(int cursor = 0; cursor < list.length; cursor++) {
						if(list[cursor].isFile()) {
							counter++;
						}
					}
					simulation.setSize(counter);
					// Get the dataSize.
					File zeroFile = new File("dataSet\\0.txt");
					if(zeroFile.exists()) {
						FileReader fileReader = new FileReader(zeroFile);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						int row=0;
						while((bufferedReader.readLine()) != null){
							row++;
						}
						bufferedReader.close();
						fileReader.close();
						System.out.println(String.format("The dataSet have %d files, %d rows per file.", counter, row));
						simulation.setDataSize(row);
						created = true;
						// Choosing type.
						System.out.println("Please input type: '1: Random', '2: Method', '3: Group'.");
						String typeString = choosingNewOrExistScanner.nextLine();
						choosingNewOrExistScanner.close();
						int type = Integer.parseInt(typeString);
						simulation.setType(type);
						simulation.read();
					} else {
						System.out.println("The dataSet is wrong, it will create a new one.");
						deleteFileOfDirectory(dataSetDirectory);
					}
				} else {
					deleteFileOfDirectory(dataSetDirectory);
				}
			} else {
				// Here not exists 'dataSet' directory.
			}
			if(!created) {
				// Create directory and create new dataSet.
				dataSetDirectory.mkdir();
				System.out.println("Please input dataSetSize dataSize and Type like '100 100 1', 100 < number < 1000, '1: Random', '2: Method', '3: Group'.");
				Scanner inputScanner = new Scanner(System.in);
				String argsInput = inputScanner.nextLine();
				String[] argsList = argsInput.split(" ");
				simulation.init(Integer.parseInt(argsList[0]),
						Integer.parseInt(argsList[1]), Integer.parseInt(argsList[2]));
			}
			Thread.sleep(1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			simulation.start();
			try {
				simulation.writeFile();
				System.out.println("The result has saved at 'Result\\report.txt'.");
			} catch (IOException ioException) {
				ioException.printStackTrace();
				System.out.println("Can not save the result.");
				simulation.printResult();
			}
		} catch (IOException ie) {
			ie.printStackTrace();
			File dataSetDirectory = new File("dataSet");
			if(dataSetDirectory.exists()) {
				deleteFileOfDirectory(dataSetDirectory);
			}
			System.out.println("Crashed files have been deleted. Please run it again.");
		}
	}
	
	public static void deleteFileOfDirectory(File fileDirectory) {
		if (fileDirectory == null || !fileDirectory.exists()){
            System.out.println("Wrong FileDirectory.");
            return;
        }
        File[] files = fileDirectory.listFiles();
        for (File f: files){
            String name = f.getName();
            System.out.println(String.format("Delete %s", name));
            if (f.isDirectory()){
                deleteFileOfDirectory(f);
            } else {
                f.delete();
            }
        }
        fileDirectory.delete();
	}
	
	private class DataCreater implements Runnable {

		int size;
		String name;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayList numberArrayList = new ArrayList();
			Random random = new Random();
			for(int cursor = this.size - 1; cursor >= 0; cursor--) {
				numberArrayList.add(cursor); // Auto boxing reverse input.
			}
				
		    for(int exchange = 0; exchange < this.size; exchange++) {
		    	double rate = random.nextDouble();
		    	while(rate == 1.0) {
		    		rate = random.nextDouble();
		    	}
		    	int current = (int)numberArrayList.get(exchange);
		    	numberArrayList.set(exchange, numberArrayList.get((int)(rate*this.size)));
		    	numberArrayList.set((int)(rate*this.size), current);
		    }
		    File dataSet = new File(String.format("dataSet\\%s.txt", this.name));
		    try {
		    	FileWriter fileWriter = new FileWriter(dataSet);
		    	for(int fileCursor = 0; fileCursor < this.size; fileCursor++) {
		    		fileWriter.write(String.format("%d\n", (int)numberArrayList.get(fileCursor)));
		    	}
		    	fileWriter.close();
	        
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		
		public void setName(int index) {
			this.name = Integer.toString(index, 10);
		}
		
		public void setSize(int size) {
			this.size = size;
		}
		
	}
	
	private class SimulationLab extends Thread {

		int type;
		HashSet drawer;
		ArrayList number;
		
		public SimulationLab() {
			this.drawer = new HashSet();
			this.number = new ArrayList();
		}
		
		@Override
		public void run() {
			switch(this.type) {
				case 1:
					this.random();
					break;
				case 2:
					this.method();
					break;
				case 3:
					this.group();
					break;
				default:
					break;
			}
		}

		public void readNumber(int index, int rows) throws IOException {
			File number = new File(String.format("dataSet\\%d.txt", index));
			if(number.exists()) {
				FileReader fileReader = new FileReader(number);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				int row=0;
				while((line = bufferedReader.readLine()) != null){
					this.number.add(Integer.parseInt(line));
					row++;
				}
				if(row != rows) {
					throw new IOException("File crashed.");
				}
				bufferedReader.close();
				fileReader.close();
			} else {
				throw new IOException("File not found.");
			}
		}

		public void setType(int type) {
			this.type = type;
		}

		public void random() {
			Random random = new Random();
			// System.out.println(this.number.toString());
			for(int person = 0; person < this.number.size(); person++) {
				boolean notFound = true;
				for(int hash = 0; hash < this.number.size(); hash++) {
					this.drawer.add(hash);
				}
				for(int open = 0; open < this.number.size() / 2; open++) {
					double rate = random.nextDouble();
					while(rate == 1.0) {
						rate = random.nextDouble();
					}
					int position = (int)(this.number.size() * rate);
					while(!this.drawer.contains(position)) {
						rate = random.nextDouble();
						while(rate == 1.0) {
							rate = random.nextDouble();
						}
						position = (int)(this.number.size() * rate);
					}
					this.drawer.remove(position);
					if(position == person) {
						notFound = false;
						break;
					}
				}
				this.drawer.clear();
				if(notFound) {
					addResult(person + 1);
					return;
				}
			}
			addResult(0);
		}

		public void method() {
			int remaining = this.number.size();
			int center = (int)(remaining / 2);
			for(int cursor = 0; cursor < this.number.size(); cursor++) {
				this.drawer.add(cursor);
			}
			int start = 0;
			while(remaining > center) {
				int length = 1;
				while(!this.drawer.contains(start)) {
					start++;
				}
				this.drawer.remove(start);
				remaining--;
				int cursor = (int)this.number.get(start);
				while(cursor != start) {
					this.drawer.remove(cursor);
					cursor = (int)this.number.get(cursor);
					length++;
					remaining--;
					if(length > center) {
						addResult(0);
						return;
					}
				}
			}
			addResult(1);
		}

		public void group() {
			int group0 = (int)(this.number.size() / 2);
			int group1 = this.number.size() - group0;
			this.method();
		}
	}
	
	private int size;
	private int dataSize;
	private int type;
	private ArrayList<SimulationLab> labsArrayList;
	volatile private ArrayList dataArrayList;
	File result;
	
	private void init(int size, int dataSize, int type) {
		this.size = size;
		this.dataSize = dataSize;
		this.type = type;
		this.labsArrayList = new ArrayList<SimulationLab>();
		this.dataArrayList = new ArrayList();
		for(int cursor = 0; cursor < size; cursor++) {
			DataCreater dataCreater = new DataCreater();
			dataCreater.setSize(dataSize);
			dataCreater.setName(cursor);
			dataCreater.run();
		}
	}

	private void read() {
		this.labsArrayList = new ArrayList<SimulationLab>();
		this.dataArrayList = new ArrayList();
	}
	
	private void setSize(int size) {
		this.size = size;
	}
	
	private void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	private void setType(int type) {
		this.type = type;
	}

	private int getSize() {
		return this.size;
	}

	private int getDataSize() {
		return this.dataSize;
	}

	private int getType() {
		return this.type;
	}

	private void randomLab() {
		for(SimulationLab simulationLab:this.labsArrayList) {
			simulationLab.start();
			try {
				simulationLab.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void methodLab() {
		for(SimulationLab simulationLab:this.labsArrayList) {
			simulationLab.start();
			try {
				simulationLab.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void groupLab() {
		for(SimulationLab simulationLab:this.labsArrayList) {
			simulationLab.start();
			try {
				simulationLab.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void start() throws IOException{

		for(int cursor = 0; cursor < this.size; cursor++) {
			SimulationLab simulationLab = new SimulationLab();
			simulationLab.setType(this.getType());
			simulationLab.readNumber(cursor, this.dataSize);
			this.labsArrayList.add(simulationLab);
		}

		switch(this.type) {
			case 1:
				this.randomLab();
				break;
			case 2:
				this.methodLab();
				break;
			case 3:
				this.groupLab();
				break;
			default:
				System.out.println("Wrong Method.");
				break;
		}
	}

	private void addResult(int result) {
		this.dataArrayList.add(result);
	}

	private void writeFile() throws IOException {
		this.result = new File("Result");
		if(!this.result.exists()) {
			this.result.mkdir();
		}
		this.result = new File("Result\\report.txt");
		if(!this.result.exists()) {
			this.result.createNewFile();
		}
		BigDecimal bigDecimal = new BigDecimal(0);
		if(this.type == 1) {
			for (int cursor = 0; cursor < this.dataArrayList.size(); cursor++) {
				if((int)this.dataArrayList.get(cursor) == 0) {
					bigDecimal = bigDecimal.add(new BigDecimal(1));
				}
			}
			System.out.println(bigDecimal.toString());
			bigDecimal = bigDecimal.multiply(new BigDecimal(100));
			bigDecimal = bigDecimal.divide(new BigDecimal(this.dataArrayList.size()), 2, RoundingMode.HALF_DOWN);
			String rate = String.valueOf(bigDecimal);
			FileWriter fileWriter = new FileWriter(this.result, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.append(String.format("Type: %d, Times: %d, DataSize: %d, Rate: %s %s.\n",
					this.type, this.size, this.dataSize, rate, "%"));
			bufferedWriter.append(this.dataArrayList.toString());
			bufferedWriter.append("\n");
			bufferedWriter.close();
			fileWriter.close();
		} else if(this.type == 2) {
			for (int cursor = 0; cursor < this.dataArrayList.size(); cursor++) {
				bigDecimal = bigDecimal.add(new BigDecimal((int) this.dataArrayList.get(cursor)));
			}
			System.out.println(bigDecimal.toString());
			bigDecimal = bigDecimal.multiply(new BigDecimal(100));
			bigDecimal = bigDecimal.divide(new BigDecimal(this.dataArrayList.size()), 2, RoundingMode.HALF_DOWN);
			String rate = String.valueOf(bigDecimal);
			FileWriter fileWriter = new FileWriter(this.result, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.append(String.format("Type: %d, Times: %d, DataSize: %d, Rate: %s %s.\n",
					this.type, this.size, this.dataSize, rate, "%"));
			bufferedWriter.append(this.dataArrayList.toString());
			bufferedWriter.append("\n");
			bufferedWriter.close();
			fileWriter.close();
		} else if(this.type == 3) {
			int center = this.dataArrayList.size();
			center = center >> 1;
			int left = 0;
			int right = 0;
			int together = 0;
			int same = 0;
			for(int cursor = 0; cursor < center; cursor++) {
				left += (int)this.dataArrayList.get(cursor);
				if((int)this.dataArrayList.get(cursor) == (int)this.dataArrayList.get(center + cursor)) {
					same++;
					if((int)this.dataArrayList.get(cursor) == 1) {
						together++;
					}
				}
			}
			for(int cursor = center; cursor < this.dataArrayList.size(); cursor++) {
				right += (int)this.dataArrayList.get(cursor);
			}
			// bigDecimal = new BigDecimal(left).divide(new BigDecimal(center), 2, RoundingMode.HALF_DOWN);
			// String g0 = bigDecimal.toString();
			// bigDecimal = new BigDecimal(right).divide(new BigDecimal(this.dataArrayList.size() - center),
			// 		2, RoundingMode.HALF_DOWN);
			// String g1 = bigDecimal.toString();
			// g0 = String.valueOf(Double.parseDouble(g0) * Double.parseDouble(g1) * 100);
			// g1 = String.valueOf((100.0 - (Double.parseDouble(g0) / Double.parseDouble(g1))) *
			// 		(1.0 - Double.parseDouble(g1)) + Double.parseDouble(g0));
			String g0 = String.valueOf(((double)together * 100 / (double)center));
			String g1 = String.valueOf(((double)same * 100 / (double)center));
			bigDecimal = new BigDecimal(left + right);
			bigDecimal = bigDecimal.multiply(new BigDecimal(100));
			bigDecimal = bigDecimal.divide(new BigDecimal(this.dataArrayList.size()), 2, RoundingMode.HALF_DOWN);
			String rate = String.valueOf(bigDecimal);
			FileWriter fileWriter = new FileWriter(this.result, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.append(String.format("Type: %d, Times: %d, DataSize: %d, Rate: %s %s, %s%s %s%s.\n",
					this.type, this.size, this.dataSize, rate, "%", g0, "%", g1, "%"));
			bufferedWriter.append(this.dataArrayList.toString());
			bufferedWriter.append("\n");
			bufferedWriter.close();
			fileWriter.close();
		}
	}

	private void printResult() {
		BigDecimal bigDecimal = new BigDecimal(0);
		if(this.type == 1) {
			for (int cursor = 0; cursor < this.dataArrayList.size(); cursor++) {
				if((int)this.dataArrayList.get(cursor) == 0) {
					bigDecimal = bigDecimal.add(new BigDecimal(1));
				}
			}
		}
		else {
			for (int cursor = 0; cursor < this.dataArrayList.size(); cursor++) {
				bigDecimal = bigDecimal.add(new BigDecimal((int) this.dataArrayList.get(cursor)));
			}
		}
		bigDecimal = bigDecimal.multiply(new BigDecimal(100));
		bigDecimal = bigDecimal.divide(new BigDecimal(this.dataArrayList.size()), 2, RoundingMode.HALF_DOWN);
		String rate = String.valueOf(bigDecimal);
		System.out.println(String.format("The rate is %s percent.", rate));
	}
}
