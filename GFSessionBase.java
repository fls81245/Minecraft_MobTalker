package net.minecraft.src;

public abstract class GFSessionBase {
	private GFSessionBase next=null;
	private  EnumFaces currentFace=EnumFaces.Normal;
	public GFSessionBase(){
		
	}	
	public GFSessionBase(EnumFaces face){
		currentFace=face;
	}
	public boolean hasNext(){
		return (next!=null);
	}
	public GFSessionBase getNext(){
		return next;
	}
	public void setNext(GFSessionBase target){
		next=target;
	}
	public EnumFaces getFace(){
		return currentFace;
	}
}
