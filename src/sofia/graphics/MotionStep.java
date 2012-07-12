package sofia.graphics;

import android.graphics.PointF;

public abstract class MotionStep
{
	public abstract void step(float timeFraction, PointF point);
	
	
	public static MotionStep constantVelocity(final float vx, final float vy)
	{
		return new MotionStep() {
			public void step(float timeFraction, PointF point)
			{
				float dx = vx * timeFraction;
				float dy = vy * timeFraction;
				
				point.x += dx;
				point.y += dy;
			}
		};
	}


	public static MotionStep constantAcceleration(
			final float vx, final float vy, final float ax, final float ay)
	{
		return new MotionStep() {
			private float velx = vx;
			private float vely = vy;

			public void step(float timeFraction, PointF point)
			{
				float dx = velx * timeFraction;
				float dy = vely * timeFraction;

				velx = velx + Math.abs(ax * timeFraction);
				vely = vely + Math.abs(ay * timeFraction);

				point.x += dx;
				point.y += dy;
			}
		};
	}
}
