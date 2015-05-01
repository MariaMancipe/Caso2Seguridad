package infracomp.caso3.client;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorSS80100 {
	private LoadGenerator generator;
	
	public GeneratorSS80100( )
	{
		Task work = createTask();
		int numberTask = 80;
		int retardo = 100;
		generator = new LoadGenerator("Generador Sin seguridad 80-100", numberTask, work, retardo);
		generator.generate();
	}

	public Task createTask() 
	{
		// TODO Auto-generated method stub
		return new ClienteSinSeguridadTask();
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		GeneratorSS80100 generador = new GeneratorSS80100();
	}
}
