package udoo;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Main {
	private static ArduinoConnection arduinoConnection;
	private static JLabel label;
	private static Container container;
	private static VideoCapture camera;
	private static boolean enableGui = true;
	private static final int WIDTH = 640;
	private static final int HEIGHT = 380;
	private static int faceCenterX = 0;
	private static int faceCenterY = 0;

	public static void main(String args[]) {
		init();
	}

	private static void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		if (enableGui) {
			JFrame jframe = new JFrame();
			jframe.setVisible(true);
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			container = jframe.getContentPane();
			jframe.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			container.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			jframe.pack();
		}

			arduinoConnection = new ArduinoConnection();
			camera = new VideoCapture(0);
			System.out.println("Camera ready");
		System.out.println("Starts capturing");
		while (true) {
			if(camera.isOpened()){
				
				captureImage();	
			}else{
				System.out.println("Camera NOT READY");
			}
		}
	}

	private static void captureImage() {
		try {
			Mat matrixImage = new Mat();
			camera.read(matrixImage);

			Imgproc.resize(matrixImage, matrixImage, new Size(WIDTH, HEIGHT));
			matrixImage = detectFace(matrixImage);
			MatOfByte matOfByte = new MatOfByte();
			Highgui.imencode(".jpg", matrixImage, matOfByte);
			InputStream in = new ByteArrayInputStream(matOfByte.toArray());
			calculateDirection();
			if (enableGui) {
				BufferedImage bufferedImage = ImageIO.read(in);
				showImage(bufferedImage);
			}

			matrixImage.release();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void calculateDirection() {
		if (faceCenterX == 0 && faceCenterY == 0) {
				arduinoConnection.serialWrite(ArduinoConnection.STOP);
				System.out.println("STOP");

		} else if (faceCenterX < (WIDTH / 2 + 100)
				&& faceCenterX > ((WIDTH / 2) - 100)) {
			arduinoConnection.serialWrite(ArduinoConnection.FORWARD);
			System.out.println("FORWARD");

		} else if (faceCenterX < (WIDTH / 2)) {
			arduinoConnection.serialWrite(ArduinoConnection.LEFT);
			System.out.println("LEFT");

		} else if (faceCenterX > (WIDTH / 2)) {
			arduinoConnection.serialWrite(ArduinoConnection.RIGHT);
			System.out.println("RIGHT");
		}
	}

	public static Mat detectFace(Mat image) {
		CascadeClassifier faceDetector = new CascadeClassifier(
				System.getProperty("user.dir") + "/lbpcascade_frontalface.xml");
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		faceCenterX = 0;
		faceCenterY = 0;
		double largestfaceSize = 0;

		for (Rect rect : faceDetections.toArray()) {
			double faceSize = Math.sqrt((Math.pow(rect.width, 2) + Math.pow(
					rect.height, 2)));

			if (faceSize > largestfaceSize) {
				faceCenterX = rect.x + rect.width / 2;
				faceCenterY = rect.y + rect.height / 2;
			}

			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}

		return image;
	}

	public static void showImage(BufferedImage bufImage) {
		if (label == null) {
			label = new JLabel(new ImageIcon(bufImage));
			container.add(label);
		} else {
			label.setIcon(new ImageIcon(bufImage));
			container.revalidate();
		}
	}

}