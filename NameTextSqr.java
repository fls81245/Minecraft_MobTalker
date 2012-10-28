package net.minecraft.src;


public class NameTextSqr {
		private EnumNamePostion postion=EnumNamePostion.LEFT;
		private String charaName=null;
		private int textColor=COLOR_DEFAULT;
		public static final int COLOR_DEFAULT=0xffffff;
		public static final int COLOR_MOB=0x79CDCD;
		public static final int COLOR_PLAYER=0x7B68EE;
		/**
		 * @return the postion
		 */
		public EnumNamePostion getPostion() {
			return postion;
		}
		/**
		 * @param postion the postion to set
		 */
		public void setPostion(EnumNamePostion postion) {
			this.postion = postion;
		}
		/**
		 * @return the charaName
		 */
		public String getCharaName() {
			return charaName;
		}
		/**
		 * @param charaName the charaName to set
		 */
		public void setCharaName(String charaName) {
			this.charaName = charaName;
		}
		public void Clear(){
			this.charaName=null;
			this.postion=EnumNamePostion.LEFT;
			textColor=this.COLOR_DEFAULT;
		}
		/**
		 * @return the textColor
		 */
		public int getTextColor() {
			return textColor;
		}
		/**
		 * @param textColor the textColor to set
		 */
		public void setTextColor(int textColor) {
			this.textColor = textColor;
		}
}
