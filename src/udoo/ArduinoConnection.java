package udoo;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class ArduinoConnection {

	public static final int STOP = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int FORWARD = 3;
	private static SerialPort serialPort;

	private static OutputStream out;

	public ArduinoConnection() {
		super();
		try {
			connect();
			System.out.println("connected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void serialWrite(int input) {
		try {
			out.write(input);
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}
	
	public boolean isReady(){
		return out != null;
	}

	public void connect() throws Exception {
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttymxc3");
		String PORT_NAMES[] = { "/dev/ttymxc3", };

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					2000);

			serialPort.setSerialPortParams(230400, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			out = serialPort.getOutputStream();

			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
}
