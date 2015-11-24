package it.diunipi.volpi.sycamore.plugins.visibilities;

import it.diunipi.volpi.sycamore.engine.Observation;
import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.gui.SycamorePanel;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem;
//import it.diunipi.volpi.sycamore.util.SycamoreUtil;

import java.util.Vector;
import java.util.concurrent.Callable;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

@PluginImplementation
public class LimitedVisibility extends VisibilityImpl<Point2D>
{
	protected Geometry				quad			= null;
	private VisibilitySettingsPanel	settingPanel	= null;

	public LimitedVisibility()
	{
		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				// setup visibility range geometry
				Material mat = new Material(SycamoreSystem.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

				// setup material parameters for transparency and face culling
				mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				mat.getAdditionalRenderState().setAlphaTest(true);
				mat.getAdditionalRenderState().setAlphaFallOff(0);
				mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

				// setup the texture
				Texture texture = SycamoreSystem.getAssetManager().loadTexture("it/diunipi/volpi/sycamore/resources/textures/circle.png");
				mat.setTexture("ColorMap", texture);

				// prepare a new quad geometry
				quad = new Geometry("Cylinder", new Quad(1, 1));
				quad.setLocalScale(getVisibilityRange());
				quad.center();
				quad.setModelBound(new BoundingBox());
				quad.updateModelBound();

				// apply the texture to the quad and set the material as fully transparent.
				TangentBinormalGenerator.generate(quad.getMesh(), true);
				quad.setMaterial(mat);
				quad.setQueueBucket(Bucket.Transparent);

				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.visibilities.VisibilityImpl#updateVisibilityGeometry()
	 */
	@Override
	public void updateVisibilityGeometry()
	{
		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				quad.setLocalScale(getVisibilityRange());

				// translate the geometry to be centered in the robot's position again.
				float translationFactor = getVisibilityRange() / 2;
				quad.setLocalTranslation(-translationFactor, -translationFactor, 0.5f);
				quad.updateGeometricState();
				return null;
			}
		});
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
	 * @see
	 * it.diunipi.volpi.sycamore.plugins.Visibility#canPointsSee(it.diunipi.volpi.sycamore.model
	 * .SycamoreAbstractPoint, it.diunipi.volpi.sycamore.model.SycamoreAbstractPoint)
	 */
	@Override
	public boolean isPointVisible(Point2D point)
	{

		Point2D center = robot.getLocalPosition();
		float visibilityRange = getVisibilityRange();
		if (center.x == point.x)
		{
			if(center.y+visibilityRange==point.y || center.y-visibilityRange==point.y)
				return true;
			else 
				return false;
		}
		else if(center.y == point.y)
		{
			if(center.x+visibilityRange==point.x || center.x-visibilityRange==point.x)
				return true;
			else 
				return false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.visibilities.Visibility#getPointInside()
	 */
	@Override
	public Point2D getPointInside()
	{
		Point2D center = robot.getLocalPosition();
		float visibilityRange = getVisibilityRange();

		int choice1=(int)(Math.random() * 2);
		int choice2=(int)(Math.random() * 2);

		float x= center.x;
		float y=center.y;

		if(choice1==1)
		{
			if(choice2==1)
			{
				return new Point2D( x, y + visibilityRange);
			}
			else
				return new Point2D( x, y-visibilityRange);
		}
		else
		{
			if(choice2==1)
			{
				return new Point2D(x + visibilityRange,y);
			}
			else
				return new Point2D( x-visibilityRange, y);
		}


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.Visibility#filter(java.util.Vector)
	 */
	@Override
	public Vector<Observation<Point2D>> filter(Vector<Observation<Point2D>> observations)
	{
		Vector<Observation<Point2D>> filtered = new Vector<Observation<Point2D>>();

		// filter observations
		for (Observation<Point2D> observation : observations)
		{
			Point2D robotPosition = observation.getRobotPosition();
			if (isPointVisible(robotPosition))
			{
				filtered.add(observation);
			}
		}

		return filtered;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.Visibility#getVisibilityRangeGeometry()
	 */
	@Override
	public Geometry getVisibilityRangeGeometry()
	{
		return quad;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getSettings()
	 */
	@Override
	public SycamorePanel getPanel_settings()
	{
		if (settingPanel == null)
		{
			settingPanel = new VisibilitySettingsPanel();
		}
		return settingPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginShortDescription()
	 */
	@Override
	public String getPluginShortDescription()
	{
		return "2D Visibility with the shape of a circle.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "2D Visibility with the shape of a circle. This visibility is modeled by a circle centered in the "
				+ "position of the robot and with a diameter equal to the visibility range. The border of the circle "
				+ "is included in the visible area. Any object whose radial distance from the robot is less than half " 
				+ "the visibility range is considered visible.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Milind Shah";
	}
}
