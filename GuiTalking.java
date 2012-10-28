package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiTalking extends GuiScreen {
	protected String[] Mesg;
	public static final int PIC_XSIZE = 300;
	public static final int PIC_YSIZE = 60;
	protected final int CHARA_IMAGE_HEIGHT = 287;
	protected final int CHARA_IMAGE_WIDTH = 230;
	private final String POS_X = "posX";
	private final String POS_Y = "posY";
	private final String WIDTH = "width";
	protected String CharaImagePath;
	protected SessionBase mainScript = null;
	protected EnumFaces face = EnumFaces.Normal;
	protected static boolean isHidden = false;
	protected Vector<String> sessionOptions = null;
	protected boolean waitingForOption = false;
	private int tick = 0;
	protected int msgShowCounts = 1;
	protected int msgCompletedIndex = -1;
	protected int textSpeed = mod_Mobtalker.mainOption.getMesgSpeed();
	public static final int TEXT_HEIGHT_UNIT = 9;
	private Properties sqrFaceProperities = null;

	public GuiTalking(SessionBase script) {

		mainScript = script;
		String posHead = mainScript.getFacePath();
		CharaImagePath = posHead + (face.toString()) + ".png";
		// sqrFaceProperities=getSqrFaceProperties(posHead);
		do {
			this.getScriptContent();
			if (mainScript.hasNext()
					&& (mainScript.hasMotion() || mainScript.changeFace()))
				showNextContent();
		} while (mainScript.hasNext()
				&& (mainScript.hasMotion() || mainScript.changeFace()));

	}

	public void drawScreen(int par1, int par2, float par3) {

		final int MAX_LINES = SessionBase.MAX_SHOWN_LINES;
		int textHeightOffset = 0;
		int j = (width - PIC_XSIZE) / 2;
		int k = (height - PIC_YSIZE - 2);
		// drawDefaultBackground();
		drawGuiContainerBackgroundLayer(j, k);
		if (!this.isHidden && Mesg != null) {
			if (SessionBase.nameSqr.getCharaName() != null) {
				NameTextSqr tmp = SessionBase.nameSqr;
				drawString(fontRenderer, tmp.getCharaName(),
						j + this.getNamePosWidth(PIC_XSIZE), k
								- TEXT_HEIGHT_UNIT, tmp.getTextColor());
			}
			for (int i = 0; i <= this.msgCompletedIndex && i < Mesg.length
					&& i < MAX_LINES; i++) {
				textHeightOffset = TEXT_HEIGHT_UNIT * i;
				drawString(fontRenderer, Mesg[i], j + 8, k + 7
						+ textHeightOffset, 0xffffff);
			}
		}
		if (this.msgCompletedIndex < Mesg.length - 1)
			drawString(fontRenderer,
					Mesg[this.msgCompletedIndex + 1].substring(0,
							this.msgShowCounts), j + 8, k + 7
							+ (msgCompletedIndex + 1) * TEXT_HEIGHT_UNIT,
					0xffffff);
		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return true;
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
		if (par2 == 1 || par2 == mc.gameSettings.keyBindInventory.keyCode) {
			isHidden = false;
			this.close();
			mc.thePlayer.closeScreen();
		}

	}

	protected void mouseClicked(int par1, int par2, int par3) {
		if (waitingForOption)
			super.mouseClicked(par1, par2, par3);
		else
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		switch (par3) {
		case 1:
			this.isHidden = !this.isHidden;
			break;
		case 0:
		default:
			/*
			 * if (this.textSpeed != 0) this.textSpeed = 0;
			 */
			if (this.isHidden)
				this.isHidden = false;
			else {
				if (mainScript.hasNext()) {
					showNextContent();
					this.tick = 0;
					if (!mainScript.hasContent()
							&& !(mainScript instanceof SessionCondition)) {
						this.close();
						mc.thePlayer.closeScreen();
					}
				} else {
					this.close();
					mc.thePlayer.closeScreen();
				}
			}

		}

	}

	private void close() {
		SessionBase.nameSqr.Clear();

	}

	protected void showNextContent() {
		boolean swaped = false;
		textSpeed = mod_Mobtalker.mainOption.getMesgSpeed();
		if (mainScript == null) {
			this.close();
			mc.thePlayer.closeScreen();
			return;
		}
		if (mainScript.hasNext()) {
			do {
				mainScript = mainScript.getNext();
				swaped = (mainScript instanceof SessionCondition);
				this.getScriptContent();
			} while (mainScript.hasNext()
					&& (mainScript.changeFace() || mainScript.hasMotion()));
		}
		/*
		 * if (!mainScript.hasNext() && !swaped) mc.thePlayer.closeScreen();
		 */
	}

	protected void drawGuiContainerBackgroundLayer(int j, int k) {
		int chara_mid = (width - CHARA_IMAGE_WIDTH) / 2;
		int i;
		int i2;
		try{
		i = mc.renderEngine.getTexture("/mobTalker_texture/square.png");
		}catch(Throwable e){
			return;
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (isFaceLocCorrect()) {
			i2 = mc.renderEngine.getTexture(CharaImagePath);
			mc.renderEngine.bindTexture(i2);
			drawChara(chara_mid, (height - (int) (CHARA_IMAGE_HEIGHT * 0.8)),
					0, 0, CHARA_IMAGE_WIDTH, CHARA_IMAGE_HEIGHT);
		}
		/*
		 * if (this.sqrFaceProperities!=null && !this.isHidden){
		 * this.drawFace(j-PIC_YSIZE, k); }
		 */
		if (!this.isHidden) {
			mc.renderEngine.bindTexture(i);
			drawTextSqr(j, k, 0, 0, PIC_XSIZE, PIC_YSIZE);

		}
		GL11.glPushMatrix();
		GL11.glTranslatef(j, k, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private boolean isFaceLocCorrect() {
		if (CharaImagePath == null)
			return false;
		InputStream inputstream = mc.texturePackList.getSelectedTexturePack()
				.getResourceAsStream(CharaImagePath);
		if (inputstream == null)
			return false;
		return true;
	}

	public void drawChara(int par1, int par2, int par3, int par4, int par5,
			int par6) {
		float f = 0.00390625F;
		float f1 = 0.00390625F * (85.5F / 100F);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(par1 + 0, par2 + par6, zLevel,
				(float) (par3 + 0) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + par6, zLevel,
				(float) (par3 + par5) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + 0, zLevel,
				(float) (par3 + par5) * f, (float) (par4 + 0) * f1);
		tessellator.addVertexWithUV(par1 + 0, par2 + 0, zLevel,
				(float) (par3 + 0) * f, (float) (par4 + 0) * f1);
		tessellator.draw();
	}

	private void drawFace(int i, int k) {
		int sourcePosX = Integer.parseInt((String) this.sqrFaceProperities
				.get(POS_X));
		int sourcePosY = Integer.parseInt((String) this.sqrFaceProperities
				.get(POS_Y));
		// int imageWidth=Integer.parseInt( (String)
		// this.sqrFaceProperities.get(WIDTH));
		drawFace(i, k, sourcePosX, sourcePosY, PIC_YSIZE, PIC_YSIZE);
	}

	/**
	 * 從素材攫取某區塊畫臉用. Args: 目標左下X點, 目標左下Y點, 來源X點, 來源Y點, 圖像寬, 圖像高
	 */
	private void drawFace(int par1, int par2, int par3, int par4, int par5,
			int par6) {
		float f = 0.00390625F;
		float f1 = 0.00390625F/* * (85.5F / 100F) */;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(par1 + 0, par2 + par6, zLevel,
				(float) (par3 + 0) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + par6, zLevel,
				(float) (par3 + par5) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + 0, zLevel,
				(float) (par3 + par5) * f, (float) (par4 + 0) * f1);
		tessellator.addVertexWithUV(par1 + 0, par2 + 0, zLevel,
				(float) (par3 + 0) * f, (float) (par4 + 0) * f1);
		tessellator.draw();
	}

	public void drawTextSqr(int par1, int par2, int par3, int par4, int par5,
			int par6) {
		float f = 0.00390625F * (85.5F / 100F);
		float f1 = 0.00390625F * 4.25F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(par1 + 0, par2 + par6, zLevel,
				(float) (par3 + 0) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + par6, zLevel,
				(float) (par3 + par5) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + 0, zLevel,
				(float) (par3 + par5) * f, (float) (par4 + 0) * f1);
		tessellator.addVertexWithUV(par1 + 0, par2 + 0, zLevel,
				(float) (par3 + 0) * f, (float) (par4 + 0) * f1);
		tessellator.draw();
	}

	protected boolean testAndSwapGui() {
		boolean result = false;
		if (mainScript instanceof SessionCondition) {
			Minecraft Corl = ModLoader.getMinecraftInstance();
			Corl.displayGuiScreen(null);
			Corl.displayGuiScreen(new GuiTalkingSelection(this.mainScript,
					this.Mesg,this.face));
			result = true;
		}
		return result;
	}

	protected void getScriptContent() {
		if (testAndSwapGui())
			return;
		if (mainScript.changeFace()) {
			face = mainScript.getFace();
			CharaImagePath = mainScript.getFacePath() + (face.toString())
					+ ".png";
		}
		if (mainScript.hasContent()) {
			textSpeed = mod_Mobtalker.mainOption.getMesgSpeed();
			Mesg = mainScript.getContent();
			this.tick = 0;
			this.msgCompletedIndex = -1;
			this.msgShowCounts = 0;
		}

		if (mainScript.hasMotion())
			mainScript.motion();
	}

	public void updateScreen() {
		final int MSG_TOTAL;
		super.updateScreen();
		if (!this.mainScript.hasContent() || this.Mesg == null) {
			if (this.Mesg != null)
				msgCompletedIndex = this.Mesg.length - 1;
			return;
		}
		if (msgCompletedIndex + 1 < Mesg.length)
			MSG_TOTAL = this.Mesg[msgCompletedIndex + 1].length();
		else {
			MSG_TOTAL = 0;
			// this.textSpeed = 0;
		}
		if (this.textSpeed == 0)
			this.msgShowCounts = MSG_TOTAL;
		if (this.msgShowCounts < MSG_TOTAL) {
			this.tick++;
			if (this.tick % this.textSpeed == 0) {
				msgShowCounts++;
			}
		} else if (this.msgCompletedIndex < this.Mesg.length - 1) {
			this.msgCompletedIndex++;
			msgShowCounts = 0;
		}
	}

	private int getNamePosWidth(int windowWidth) {
		NameTextSqr tmp = SessionBase.nameSqr;
		switch (tmp.getPostion()) {
		case MIDDLE:
			return (windowWidth - fontRenderer.getStringWidth(tmp
					.getCharaName())) / 2;
		case RIGHT:
			return windowWidth
					- fontRenderer.getStringWidth(tmp.getCharaName());
		case LEFT:
		default:
			return fontRenderer.FONT_HEIGHT;
		}
	}
}
