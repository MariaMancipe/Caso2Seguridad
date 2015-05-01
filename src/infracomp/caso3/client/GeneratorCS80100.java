package infracomp.caso3.client;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorCS80100 {

	private LoadGenerator generator;
	
	public GeneratorCS80100( )
	{
		Task work = createTask();
		int numberTask = 80;
		int retardo = 100;
		generator = new LoadGenerator("Generador con seguridad 80-100", numberTask, work, retardo);
		generator.generate();
	}

	public Task createTask() 
	{
		// TODO Auto-generated method stub
		return new ClienteSeguridadTask();
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		GeneratorCS80100 generador = new GeneratorCS80100();
	}
}
