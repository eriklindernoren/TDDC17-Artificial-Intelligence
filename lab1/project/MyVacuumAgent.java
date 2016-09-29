package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.search.framework.Problem;
import aima.core.search.uninformed.BreadthFirstSearch;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.ArrayList;
import java.util.Random;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH 	= 0;
	public static final int EAST 	= 1;
	public static final int SOUTH 	= 2;
	public static final int WEST 	= 3;
	public int agent_direction 		= EAST;
	
	public int home_x = 0;
	public int home_y = 0;
	
	public int unknown_x = -1;
	public int unknown_y = -1;
	
	public int turns = 0;
		
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (!bump && agent_last_action == 1)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public ArrayList<Integer> findUnexploredAreas()
	{
		ArrayList<Integer> unexploredPositions = new ArrayList<Integer>();
		
		int x_pos = -1;
		for(int y = 1; y < 29; y++){
			for(int x = 1; x < 29; x++){
				if(world[x][y] == UNKNOWN && x_pos == -1){
					x_pos = x;
				}
				if((world[x][y] == CLEAR) && x_pos != -1){
					unexploredPositions.add(x_pos);
					unexploredPositions.add(y);
					return unexploredPositions;
				}
			}
			x_pos = -1;
		}
				
		return unexploredPositions;
	}
	
	public boolean isSurrounded(){
		boolean wallToTheLeft = world[agent_x_position-1][agent_y_position] == WALL ||
				world[agent_x_position-1][agent_y_position] == CLEAR;
		boolean wallToTheRight = world[agent_x_position+1][agent_y_position] == WALL ||
				world[agent_x_position+1][agent_y_position] == CLEAR;
		boolean wallOver = world[agent_x_position][agent_y_position-1] == WALL ||
				world[agent_x_position][agent_y_position-1] == CLEAR;
		boolean wallUnder = world[agent_x_position][agent_y_position+1] == WALL ||
				world[agent_x_position][agent_y_position+1] == CLEAR;
		boolean surrounded = wallToTheLeft && wallToTheRight && wallOver && wallUnder;
		return surrounded;
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 10;
	public MyAgentState state = new MyAgentState();
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	System.out.println("Unk_x=" + state.unknown_x);
    	System.out.println("Unk_y=" + state.unknown_y);
    	
//	    iterationCounter--;
//	    
//	    if (iterationCounter==0)
//	    	return NoOpAction.NO_OP;	    

	    
	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
	    	System.out.println(state.agent_direction);
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    else if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else{
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    }
	    	
	    
	    state.printWorldDebug();
	    	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    else
	    {
	    	boolean searchingForUnknown = (state.unknown_x != -1);
	    	if (bump)
	    	{
	    		state.agent_direction++;
	    		state.agent_direction%=4;
	    		System.out.println("Turning right");
	    		state.agent_last_action=state.ACTION_TURN_RIGHT;
		    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    	}
	    	else if(searchingForUnknown){
	    		
	    		boolean wallToTheLeft = state.world[state.agent_x_position-1][state.agent_y_position] == state.WALL;
	    		boolean wallToTheRight = state.world[state.agent_x_position+1][state.agent_y_position] == state.WALL;
	    		boolean wallOver = state.world[state.agent_x_position][state.agent_y_position-1] == state.WALL;
	    		boolean wallUnder = state.world[state.agent_x_position-1][state.agent_y_position+1] == state.WALL;
	    		
	    		if(!wallToTheLeft && state.agent_x_position > state.unknown_x){
	    			if(!(state.agent_direction == state.WEST)){
	    				if(state.agent_direction == state.NORTH){
	    					state.agent_direction = state.WEST;
	    					state.agent_last_action=state.ACTION_TURN_LEFT;
		    		    	return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	    				}
	    				else{
	    					state.agent_direction++;
	    		    		state.agent_direction%=4;
	    		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
	    			    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    				}
	    			}
	    		}
	    		else if(!wallToTheRight && state.agent_x_position < state.unknown_x){
	    			if(!(state.agent_direction == state.EAST)){
	    				if(state.agent_direction == state.SOUTH){
	    					state.agent_direction = state.EAST;
	    					state.agent_last_action=state.ACTION_TURN_LEFT;
		    		    	return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	    				}
	    				else{
	    					state.agent_direction++;
	    		    		state.agent_direction%=4;
	    		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
	    			    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    				}
	    			}
	    		}
	    		else if(!wallUnder && state.agent_y_position < state.unknown_y){
	    			if(!(state.agent_direction == state.SOUTH)){
	    				if(state.agent_direction == state.WEST){
	    					state.agent_direction = state.SOUTH;
	    					state.agent_last_action=state.ACTION_TURN_LEFT;
		    		    	return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	    				}
	    				else{
	    					state.agent_direction++;
	    		    		state.agent_direction%=4;
	    		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
	    			    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    				}
	    			}
	    		}
	    		else if(!wallOver && state.agent_y_position > state.unknown_y){
	    			if(!(state.agent_direction == state.NORTH)){
	    				if(state.agent_direction == state.EAST){
	    					state.agent_direction = state.NORTH;
	    					state.agent_last_action=state.ACTION_TURN_LEFT;
		    		    	return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	    				}
	    				else{
	    					state.agent_direction++;
	    		    		state.agent_direction%=4;
	    		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
	    			    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    				}
	    			}
	    		}
	    		int x_pos = state.agent_x_position;
		    	int y_pos = state.agent_y_position;
		    	switch (state.agent_direction) {
				case MyAgentState.NORTH:
					y_pos--;
					break;
				case MyAgentState.EAST:
					x_pos++;
					break;
				case MyAgentState.SOUTH:
					y_pos++;
					break;
				case MyAgentState.WEST:
					x_pos--;
					break;
				}
		    	if(x_pos == state.unknown_x && y_pos == state.unknown_y){
		    		state.unknown_x = -1;
		    		state.unknown_y = -1;
		    	}
		    	else if(state.world[x_pos][y_pos] == state.WALL){
		    		state.agent_direction++;
		    		state.agent_direction%=4;
		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
			    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		    	}
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
	    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    		
	    	}
	    	else
	    	{
	    		int x_pos = state.agent_x_position;
		    	int y_pos = state.agent_y_position;
		    	switch (state.agent_direction) {
				case MyAgentState.NORTH:
					y_pos--;
					break;
				case MyAgentState.EAST:
					x_pos++;
					break;
				case MyAgentState.SOUTH:
					y_pos++;
					break;
				case MyAgentState.WEST:
					x_pos--;
					break;
				}
		    	boolean visited = (state.world[x_pos][y_pos] == state.CLEAR || state.world[x_pos][y_pos] == state.WALL);
		    	if(visited){
		    		if(state.isSurrounded()){
		    			ArrayList<Integer> pos = state.findUnexploredAreas();
		    			for(int x: pos)
		    				System.out.println("Coord: " + x);
		    			
		    			if(!pos.isEmpty()){
		    				state.unknown_x = pos.get(0);
		    				state.unknown_y = pos.get(1);
		    				return LIUVacuumEnvironment.ACTION_SUCK;
		    			}
		    			return NoOpAction.NO_OP;
		    		}
		    		state.agent_direction++;
		    		state.agent_direction%=4;
		    		System.out.println("Turning right");
		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
			    	return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		    	}else{
		    		state.turns = 0;
		    		System.out.println("Going forward");
		    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		    	}
	    	}
	    }
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
