package infracomp.caso3.client;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorCS40020 {
	private LoadGenerator generator;
	
	public GeneratorCS40020( )
	{
		Task work = createTask();
		int numberTask = 400;
		int retardo = 20;
		generator = new LoadGenerator("Generador con seguridad 400-20", numberTask, work, retardo);
		generator.generate();
	}

	public Task createTask() 
	{
		// TODO Auto-generated method stub
		return new ClienteSeguridadTask();
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		GeneratorCS40020 generador = new GeneratorCS40020();
	}
}
