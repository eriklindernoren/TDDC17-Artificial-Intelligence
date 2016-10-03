public class TutorialController extends Controller {

    public SpringObject object;

    ComposedSpringObject cso;

    /* These are the agents senses (inputs) */
	DoubleFeature x; /* Positions */
	DoubleFeature y;
	DoubleFeature vx; /* Velocities */
	DoubleFeature vy;
	DoubleFeature angle; /* Angle */

    /* Example:
     * x.getValue() returns the vertical position of the rocket 
     */

	/* These are the agents actuators (outputs)*/
	RocketEngine leftRocket;
	RocketEngine middleRocket;
	RocketEngine rightRocket;

    /* Example:
     * leftRocket.setBursting(true) turns on the left rocket 
     */
	
	public void init() {
		cso = (ComposedSpringObject) object;
		x = (DoubleFeature) cso.getObjectById("x");
		y = (DoubleFeature) cso.getObjectById("y");
		vx = (DoubleFeature) cso.getObjectById("vx");
		vy = (DoubleFeature) cso.getObjectById("vy");
		angle = (DoubleFeature) cso.getObjectById("angle");

		leftRocket = (RocketEngine) cso.getObjectById("rocket_engine_left");
		rightRocket = (RocketEngine) cso.getObjectById("rocket_engine_right");
		middleRocket = (RocketEngine) cso.getObjectById("rocket_engine_middle");
	}

    public void tick(int currentTime) {
    	
		String yDir = (vy.getValue() < 0) ? "N": "S";
		String xDir = (vx.getValue() < 0) ? "W": "E";
		double yVel = Math.abs(vy.getValue());
		double xVel = Math.abs(vx.getValue());
		
		String dir = String.format("%s%s", yDir, xDir);
		double vel = Math.sqrt(Math.pow(xVel, 2) + Math.pow(yVel, 2));
		
		
//    	System.out.println("Angle: " + angle.getValue());
//    	System.out.println("Velocity X: " + vx.getValue());
//    	System.out.println("Velocity Y: " + vy.getValue());
		System.out.println(String.format("Direction: %s, Velocity: %s", dir, vel + ""));
//		System.out.println("----");

		
    	if(angle.getValue() < -0.0095)
    		leftRocket.setBursting(true);
    	else
    		leftRocket.setBursting(false);
    	if(angle.getValue() > 0.0095)
    		rightRocket.setBursting(true);
    	else
    		rightRocket.setBursting(false);
    	if(vy.getValue() > 0.04)
    		middleRocket.setBursting(true);  
    	else
    		middleRocket.setBursting(false);
    }

}
