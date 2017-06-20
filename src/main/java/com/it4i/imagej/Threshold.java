package com.it4i.imagej;
/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.Previewable;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.widget.NumberWidget;

import jnr.ffi.Struct.uid_t;

import java.io.File;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs, and
 * replace the {@link run} method implementation with your own logic.
 * </p>
 */
@Plugin(type = Command.class, menuPath = "Plugins>Threshold")
public class Threshold<T extends RealType<T>> implements Command, Previewable {

	@Parameter
	private DatasetService dss;

	@Parameter
	private LogService ls;

	@Parameter
	private UIService uiService;

	@Parameter
	private OpService ops;

	@Parameter(type = ItemIO.INPUT)
	private Dataset image;

	@Parameter(type = ItemIO.OUTPUT)
	private Dataset result;

	@Parameter(label = "Threshold", persist = false, style = NumberWidget.SLIDER_STYLE, min = "1", max = "255.0", stepSize = "1.0")
	private double threshold;

	public void preview() {
		ls.info("Run");
		boolean first = result == null;
		run();
		if (first)
			uiService.show(result);
		else
			result.update();
	}

	public void cancel() {
		result = image.duplicate();
	}

	private void computeThreshold() {

		ls.info("Thresholding, threshold = " + threshold);

		Dataset in = image;
		if (result == null)
			result = image.duplicate();

		Cursor<RealType<?>> cursorIn = in.localizingCursor();
		RandomAccess<RealType<?>> cursorOut = result.randomAccess();

		double val = 0;
		RealType<?> type;

		while (cursorIn.hasNext()) {
			type = cursorIn.next();
			val = type.getRealDouble();

			cursorOut.setPosition(cursorIn);
			cursorOut.get().setReal(val > threshold ? 1 : 0);

		}

	}

	@Override
	public void run() {
		computeThreshold();
	}

	/**
	 * This main function serves for development purposes. It allows you to run
	 * the plugin immediately out of your integrated development environment
	 * (IDE).
	 *
	 * @param args
	 *            whatever, it's ignored
	 * @throws Exception
	 */
	public static void main(final String... args) throws Exception {
		// create the ImageJ application context with all available services
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// Date d = new Date();
		// ij.ui().showDialog("Hello, it is " + d.toLocaleString());

		// ask the user for a file to open
		// final File file = ij.ui().chooseFile(null, "open");
		final File file = new File("/tmp/clowngray.png");

		if (file != null) {
			// load the dataset
			final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

			// show the image
			ij.ui().show(dataset);

			// invoke the plugin
			// ij.command().run(Threshold.class, true);
		}

	}

}
