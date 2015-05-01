package infracomp.caso3.client;

import uniandes.gload.core.Task;

public class ClienteSinSeguridadTask extends Task{

	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
		System.out.println("Hub� un fallo");
		
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		System.out.println(Task.OK_MESSAGE);
		System.out.println("Se envi� todo");
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		ClienteSS.comunicacion();
	}

}
