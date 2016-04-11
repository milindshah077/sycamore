/**
 * 
 */
package it.diunipi.volpi.sycamore.plugins.agreements;

import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot;
import it.diunipi.volpi.sycamore.gui.SycamorePanel;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem;
import it.diunipi.volpi.sycamore.util.PropertyManager;
import it.diunipi.volpi.sycamore.util.SycamoreProperty;
import it.diunipi.volpi.sycamore.util.SycamoreUtil;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.concurrent.Callable;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;

/**
 * Consistent compass in 2D. The directions and orientations of all the axes are agreed between the
 * robots. The compass is defined consistent as the definitions of north, south, east and west are
 * the same for all the robots. In terms of of transformation factors, the rotation factor is
 * completely agreed between robots, as well as the sign of the scale factor along each axis. The
 * other elements (translation factor and scale factor) are different between a robot and another.
 * 
 * @author Milind Shah, Sunil Singh 
 */
@PluginImplementation
public class ConsistentCompass_GridPoints2D extends AgreementImpl<Point2D>
{
	/**
	 * Properties related to consistent compass agreement 2D
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private enum ConsistentCompass2DProperties implements SycamoreProperty
	{
		CONSISTENT_COMPASS_2D_FLIP_X("FlipX", false + ""), 
		CONSISTENT_COMPASS_2D_FLIP_Y("FlipY", false + ""), 
		CONSISTENT_COMPASS_2D_ROTATION("Rotation", "" + 0.0);

		private String	description		= null;
		private String	defaultValue	= null;

		/**
		 * Constructor.
		 */
		ConsistentCompass2DProperties(String description, String defaultValue)
		{
			this.description = description;
			this.defaultValue = defaultValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see it.diunipi.volpi.sycamore.util.SycamoreProperty#getDescription()
		 */
		@Override
		public String getDescription()
		{
			return description;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see it.diunipi.volpi.sycamore.util.SycamoreProperty#getDefaultValue()
		 */
		@Override
		public String getDefaultValue()
		{
			return defaultValue;
		}
	}

	private Node								axesNode		= new Node("Axes node");

	private int								translationX	= SycamoreUtil.getRandomInt(-4, 4);
	private int								translationY	= SycamoreUtil.getRandomInt(-4, 4);
	
	private double								scaleFactor		= SycamoreUtil.getRandomDouble(0.5, 4);

	private ConsistentCompass2DSettingsPanel	panel_settings	= null;

	/**
	 * Default constructor
	 */
	public ConsistentCompass_GridPoints2D()
	{
		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				// red arrow for x axis
				Arrow arrowX = new Arrow(new Vector3f(2, 0, 0));
				arrowX.setLineWidth(4); // make arrow thicker
				Geometry xAxis = new Geometry("X coordinate axis", arrowX);
				Material matX = new Material(SycamoreSystem.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
				matX.getAdditionalRenderState().setWireframe(true);
				matX.setColor("Color", new ColorRGBA(0.7f, 0, 0, 1));
				xAxis.setMaterial(matX);
				xAxis.setLocalTranslation(Vector3f.ZERO);
				axesNode.attachChild(xAxis);

				// green arrow for y axis
				Arrow arrowY = new Arrow(new Vector3f(0, 2, 0));
				arrowY.setLineWidth(4); // make arrow thicker
				Geometry yAxis = new Geometry("Y coordinate axis", arrowY);
				Material matY = new Material(SycamoreSystem.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
				matY.getAdditionalRenderState().setWireframe(true);
				matY.setColor("Color", new ColorRGBA(0, 0.7f, 0, 1));
				yAxis.setMaterial(matY);
				yAxis.setLocalTranslation(Vector3f.ZERO);
				axesNode.attachChild(yAxis);

				axesNode.updateGeometricState();

				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.plugins.agreements.Agreement#toLocalCoordinates(it.diunipi.volpi
	 * .sycamore.model.SycamoreAbstractPoint, it.diunipi.volpi.sycamore.model.SycamoreAbstractPoint)
	 */
	@Override
	public Point2D toLocalCoordinates(Point2D point)
	{
		// prepare awt Point2D points
		java.awt.geom.Point2D sourcePoint = SycamoreUtil.convertPoint2D(point);
		java.awt.geom.Point2D destPoint = new java.awt.geom.Point2D.Float();

		try
		{
			// transform using the inverse transform the source point into the dest point
			computeTransform().inverseTransform(sourcePoint, destPoint);
		}
		catch (NoninvertibleTransformException e)
		{
			e.printStackTrace();
		}

		// return dest point as a Sycamore Point2D object
		return SycamoreUtil.convertPoint2D(destPoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.plugins.agreements.Agreement#toGlobalCoordinates(it.diunipi.volpi
	 * .sycamore.model.SycamoreAbstractPoint, it.diunipi.volpi.sycamore.model.SycamoreAbstractPoint)
	 */
	@Override
	public Point2D toGlobalCoordinates(Point2D point)
	{
		// prepare awt Point2D points
		java.awt.geom.Point2D sourcePoint = SycamoreUtil.convertPoint2D(point);
		java.awt.geom.Point2D destPoint = new java.awt.geom.Point2D.Float();

		// transform the source point into the dest point
		computeTransform().transform(sourcePoint, destPoint);

		// return dest point as a Sycamore Point2D object
		return SycamoreUtil.convertPoint2D(destPoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.agreements.Agreement#getLocaTranslation()
	 */
	@Override
	public Vector3f getLocalTranslation()
	{
		return new Vector3f((float) translationX, (float) translationY, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.agreements.Agreement#getLocalRotation()
	 */
	@Override
	public Quaternion getLocalRotation()
	{
		float angleX = 0;
		float angleY = 0;
		float angleZ = (float) Math.toRadians(getRotation());

		return new Quaternion(new float[]
		{ angleX, angleY, angleZ });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.agreements.Agreement#getLocalScale()
	 */
	@Override
	public Vector3f getLocalScale()
	{
		return new Vector3f((float) getScaleX(), (float) getScaleY(), 0);
	}

	/**
	 * Returns an AffineTransform object that describe the transform of the system
	 * 
	 * @return
	 */
	private AffineTransform computeTransform()
	{
		AffineTransform transform = new AffineTransform();
		transform.translate((double)translationX, (double)translationY);
		transform.rotate(Math.toRadians(getRotation()));
		transform.scale(getScaleX(), getScaleY());

		return transform;
	}

	/**
	 * @return the sign of the scale on x axis
	 */
	private int getSignX()
	{
		return (isFlipX() ? -1 : 1);
	}

	/**
	 * @return the sign of the scale on y axis
	 */
	private int getSignY()
	{
		return (isFlipY() ? -1 : 1);
	}

	/**
	 * @return the scaleX
	 */
	public double getScaleX()
	{
		if (AgreementImpl.isFixMeasureUnit())
		{
			return getSignX();
		}
		else
		{
			return scaleFactor * (getSignX());
		}
	}

	/**
	 * @return the scaleY
	 */
	public double getScaleY()
	{
		if (AgreementImpl.isFixMeasureUnit())
		{
			return getSignY();
		}
		else
		{
			return scaleFactor * (getSignY());
		}
	}

	/**
	 * @return the rotation
	 */
	public static boolean isFlipX()
	{
		return PropertyManager.getSharedInstance().getBooleanProperty(ConsistentCompass2DProperties.CONSISTENT_COMPASS_2D_FLIP_X);
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public static void setFlipX(Boolean flipX)
	{
		PropertyManager.getSharedInstance().putProperty(ConsistentCompass2DProperties.CONSISTENT_COMPASS_2D_FLIP_X, flipX);
	}

	/**
	 * @return the rotation
	 */
	public static boolean isFlipY()
	{
		return PropertyManager.getSharedInstance().getBooleanProperty(ConsistentCompass2DProperties.CONSISTENT_COMPASS_2D_FLIP_Y);
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public static void setFlipY(Boolean flipY)
	{
		PropertyManager.getSharedInstance().putProperty(ConsistentCompass2DProperties.CONSISTENT_COMPASS_2D_FLIP_Y, flipY);
	}

	/**
	 * @return the rotation
	 */
	public static double getRotation()
	{
		return PropertyManager.getSharedInstance().getDoubleProperty(ConsistentCompass2DProperties.CONSISTENT_COMPASS_2D_ROTATION);
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public static void setRotation(double rotation)
	{
		PropertyManager.getSharedInstance().putProperty(ConsistentCompass2DProperties.CONSISTENT_COMPASS_2D_ROTATION, rotation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.agreements.Agreement#getAxesNode()
	 */
	@Override
	public Node getAxesNode()
	{
		return axesNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamoreTypedPlugin#getType()
	 */
	@Override
	public TYPE getType()
	{
		return TYPE.TYPE_2D;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Milind Shah, Sunil Singh";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginShortDescription()
	 */
	@Override
	public String getPluginShortDescription()
	{
		return "Consistent compass in 2D. North, south, west, east are the same for all the robots.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "Consistent compass in 2D. The directions and orientations of all the axes are agreed between the robots. The compass is defined " +
				"consistent as the definitions of north, south, east and west are the same for all the robots. In terms of of transformation factors, " +
				"the rotation factor is completely agreed between robots, as well as the sign of the scale factor along each axis. The other elements " +
				"(translation factor and scale factor) are different between a robot and another.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPanel_settings()
	 */
	@Override
	public SycamorePanel getPanel_settings()
	{
		if (panel_settings == null)
		{
			panel_settings = new ConsistentCompass2DSettingsPanel();
		}
		return panel_settings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.plugins.agreements.Agreement#setOwner(it.diunipi.volpi.sycamore
	 * .model.SycamoreRobot)
	 */
	@Override
	public void setRobot(SycamoreRobot<Point2D> owner)
	{
		// Nothing to do
	}
}
