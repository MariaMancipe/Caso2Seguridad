package infracomp.caso3.client;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorSS20040 {

	private LoadGenerator generator;
	
	public GeneratorSS20040( )
	{
		Task work = createTask();
		int numberTask = 200;
		int retardo = 40;
		generator = new LoadGenerator("Generador Sin seguridad 200-40", numberTask, work, retardo);
		generator.generate();
	}

	public Task createTask() 
	{
		// TODO Auto-generated method stub
		return new ClienteSinSeguridadTask();
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		GeneratorSS20040 generador = new GeneratorSS20040();
	}
}
