package net.minecraft.src;
import java.io.*;

public class SessionText extends SessionBase {
	private static String PLAYNAME_REPLACE_CODE="(playername)";
	private final int TALKING_SQR_WIDTH=280;
	private String[] content;
	public SessionText() {
		super();
	}
	public SessionText(String defaultSpeech){
		this.content=defaultSpeech.split("$");
		replacePlayerName();
	}

	@Override
	protected SessionBase addCode(String code) {
		FontRenderer fontRender=ModLoader.getMinecraftInstance().fontRenderer;
		String overTmp=null;
		this.content=code.split("::");
		for (int i=0;i<this.content.length;i++){
			int overCount=0;
			while(fontRender.getStringWidth(this.content[i].substring(0, content[i].length()-overCount))>TALKING_SQR_WIDTH) overCount++;
			if (overCount!=0) {
				overTmp=content[i].substring(content[i].length()-overCount);
				content[i]=content[i].substring(0, content[i].length()-overCount);
				String[] ansArray=new String[content.length+1];
				for (int j=0;j<=i;j++) ansArray[j]=content[j];
				ansArray[i+1]=overTmp;
				for (int j=i+1;j<content.length;j++) ansArray[j+1]=content[j];
				this.content=ansArray;
			}
			overTmp=null;
		}
		if (this.content.length>this.MAX_SHOWN_LINES){
			String[] resultTmp=new String[this.MAX_SHOWN_LINES];
			for (int i=0;i<this.content.length;i++){
				if (i<this.MAX_SHOWN_LINES) resultTmp[i]=new String(this.content[i]);
				else{
					resultTmp[this.MAX_SHOWN_LINES-1]=resultTmp[this.MAX_SHOWN_LINES-1]+this.content[i];
				}
			}
			this.content=resultTmp;
		}
		replacePlayerName();
		return this;
	}

	private void replacePlayerName() {
		if (this.playerName==null || this.playerName.isEmpty()) return;
		if (this.content==null || this.content.length<=0) return;
		for (int i=0;i<content.length;i++){
			if (this.content[i].contains(PLAYNAME_REPLACE_CODE)){
				this.content[i]=this.content[i].replaceAll(PLAYNAME_REPLACE_CODE, this.playerName).replace("(", "").replace(")", "");
			}
		}

		
	}
	@Override
	public String[] getContent() {
		replacePlayerName();
		return this.content;
	}

	@Override
	public boolean hasContent() {
		return (content!=null);
	}

	@Override
	public EnumFaces getFace() {
		return null;
	}

	@Override
	public boolean changeFace() {
		return false;
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
