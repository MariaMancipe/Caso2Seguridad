package infracomp.caso3.client;

import uniandes.gload.core.Task;

public class ClienteSeguridadTask extends Task {
	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
		System.out.println("Hubó un fallo");
		
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		System.out.println(Task.OK_MESSAGE);
		System.out.println("Se envió todo");
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		ClienteCS.comunicacion();
	}
}
