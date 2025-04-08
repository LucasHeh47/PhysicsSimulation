package com.lucasj.PhysicsSimulation;

import com.lucasj.PhysicsSimulation.Simulation.Particle;

public class Debug {

	public static void Log(Particle obj, Object obj2) {
		System.out.println("[" + obj.getClass().getSimpleName() + "] " + obj2);
	}
	
}
