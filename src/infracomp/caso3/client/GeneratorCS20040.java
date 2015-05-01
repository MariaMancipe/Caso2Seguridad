package infracomp.caso3.client;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorCS20040 {
private LoadGenerator generator;
	
	public GeneratorCS20040( )
	{
		Task work = createTask();
		int numberTask = 200;
		int retardo = 40;
		generator = new LoadGenerator("Generador con seguridad 200-40", numberTask, work, retardo);
		generator.generate();
	}

	public Task createTask() 
	{
		// TODO Auto-generated method stub
		return new ClienteSeguridadTask();
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
		GeneratorCS20040 generador = new GeneratorCS20040();
	}
}
