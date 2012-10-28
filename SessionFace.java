package net.minecraft.src;

import java.io.BufferedReader;

public class SessionFace extends SessionBase {

	private static final String FACE_CODE="#FACE";
	protected EnumFaces face=EnumFaces.Normal;
	public SessionFace() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SessionBase addCode(String code) {
		if (!code.contains(FACE_CODE)) return this;
		String subCode=code.substring(FACE_CODE.length()+1);
		subCode.replace(" ", "");
		subCode=subCode.substring(0, 1).toUpperCase()+subCode.substring(1).toLowerCase();
		try{
			this.face=EnumFaces.valueOf(subCode);
		}catch(Throwable e){};
		if (face==null)face=EnumFaces.Normal;
		return this;
		
	}

	@Override
	public String[] getContent() {
		return null;
	}


	@Override
	public boolean hasContent() {
		return false;
	}


	@Override
	public EnumFaces getFace() {
		return this.face;
	}


	@Override
	public boolean changeFace() {
		return true;
	}


	@Override
	public boolean hasMotion() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void motion() {
		// TODO Auto-generated method stub
		
	}

}
