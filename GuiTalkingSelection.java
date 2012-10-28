package net.minecraft.src;

import java.util.Iterator;
import java.util.Vector;

import net.minecraft.client.Minecraft;

public class GuiTalkingSelection extends GuiTalking {
	private final int BUTTON_HEIGHT=20;
	private final int BUTTON_DEFAULT_WIDTH=200;
	public GuiTalkingSelection(SessionBase script,String[] preLoadMsg,EnumFaces facePar){
		this(script,facePar);
		this.Mesg=preLoadMsg;
		this.textSpeed=0;
		this.face=facePar;
	}
	public GuiTalkingSelection(SessionBase script,EnumFaces facePar) {
		
		super(script);
		SessionCondition condSession=(SessionCondition)mainScript;
		this.sessionOptions=condSession.optionText;
		waitingForOption=true;
		this.face=facePar;
		CharaImagePath = mainScript.getFacePath() + (face.toString())
				+ ".png";
		// TODO Auto-generated constructor stub
	}
    public void initGui()
    {
    	controlList.clear();
		this.updateControlList();
    }
    protected void updateControlList(){
    	final int heightInit=height / 4;
    	final int widthPos;
    	final int diff=24;
    	int buttonHeightPos=heightInit+48;
    	if (this.sessionOptions==null) controlList.clear();
    	else{
    		int contSize=sessionOptions.size();
    		int buttonWidth=getButtonWidth(sessionOptions);
    		widthPos=(width-buttonWidth)/2;
    		switch (contSize){
    			case 1:
    				buttonHeightPos+=diff;
    			case 2:
    				buttonHeightPos+=(diff/2);
    		}
    		for (int i=0;i<contSize && i<3;i++){
    			controlList.add(new GuiButton(i+1,widthPos, buttonHeightPos,buttonWidth,BUTTON_HEIGHT,this.sessionOptions.get(i)));
    			buttonHeightPos+=diff;
    		}
    	}
    }
    private int getButtonWidth(Vector<String> parmOptions) {
    		int result=BUTTON_DEFAULT_WIDTH;
    		int tmp;
    		for (int i=0;i<parmOptions.size();i++){
    			tmp=this.fontRenderer.getStringWidth(parmOptions.get(i));
    			if (tmp>result) result=tmp;
    		}
    		return result;
	}
	protected void actionPerformed(GuiButton par1GuiButton)
    {
    	((SessionCondition)this.mainScript).optionToken(par1GuiButton.id-1);
    	testAndSwapGui();
    }
    
	protected boolean testAndSwapGui() {
		boolean result = false;
		if (!(mainScript instanceof SessionCondition)) {
			Minecraft Corl = ModLoader.getMinecraftInstance();
			Corl.displayGuiScreen(null);
			Corl.displayGuiScreen(new GuiTalking(this.mainScript));
			result=true;
		}
		return result;
	}
	public void updateScreen() {
		super.updateScreen();
		if (this.msgCompletedIndex>=0)this.msgShowCounts=this.Mesg[this.msgCompletedIndex].length();
		else this.msgShowCounts=this.Mesg[0].length();
	}
}
