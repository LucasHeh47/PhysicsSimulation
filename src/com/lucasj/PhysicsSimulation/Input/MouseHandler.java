package com.lucasj.PhysicsSimulation.Input;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.lucasj.PhysicsSimulation.Math.Vector2D;
import com.lucasj.PhysicsSimulation.Simulation.Particle;
import com.lucasj.PhysicsSimulation.Simulation.Simulation;

public class MouseHandler implements MouseMotionListener, MouseListener {

	private Simulation sim;
	public boolean pullOrPush = true; // true = pull, false = push
	
	public MouseHandler(Simulation sim) {
		this.sim = sim;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		sim.setInputForce(new Vector2D(e.getX(), e.getY()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) pullOrPush = true;
		else pullOrPush = false;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		sim.setInputForce(new Vector2D(-1, -1));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
