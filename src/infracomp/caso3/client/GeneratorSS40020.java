package infracomp.caso3.client;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorSS40020 {

	private LoadGenerator generator;
	
	public GeneratorSS40020( )
	{
		Task work = createTask();
		int numberTask = 400;
		int retardo = 20;
		generator = new LoadGenerator("Generador Sin seguridad 400-20", numberTask, work, retardo);
		generator.generate();
	}

	public Task createTask() 
	{
		// TODO Auto-generated method stub
		return new ClienteSinSeguridadTask();
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		GeneratorSS40020 generador = new GeneratorSS40020();
	}
}
