package SyScIslands;

import java.util.LinkedList;
import java.util.List;

import eawag.model.Agent;
import eawag.model.Swarm;

public class Dorf extends Swarm {

	private int nahrung;
	private int holz;
	public int wasser;
	public boolean hafen;
	public List<Schiff> schiffe;
	public int xPos, yPos;
	public int schiffBauzeit;
	
	private Object nahrungLock = new Object();
	private Object holzLock = new Object();

	public Dorf(int xPos, int yPos) {
		this(xPos, yPos, 10);
	}

	public Dorf(int xPos, int yPos, int siedlerAnz) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.nahrung = 0;
		this.holz = 0;
		this.wasser = 0;
		this.hafen = false;
		this.schiffe = new LinkedList<Schiff>();
		for (int i = 0; i < siedlerAnz; i++) {
			siedlerHinzufuegen(new Siedler());
		}
	}

	@Override
	public void condition() {
		super.condition();
	}

	@Override
	public void action() {
		if (getAnzahlSiedler() == 0) {
			Insel insel = getInsel();
			if (insel != null)
				insel.entferneDorf();
		}
	}

	public synchronized void siedlerHinzufuegen(Siedler siedler) {
		if (siedler.getFather() != this)
			siedler.join(this);
	}

	public synchronized void siedlerEntfernen(Siedler siedler) {
		if (siedler.getFather() == this) {
			siedler.leave();
		}
	}

	public int getAnzahlSiedler() {
		int siedler = 0;
		for (int i = 0; i < getChildCount(); i++)
			if (getChildAt(i) instanceof Siedler)
				siedler++;

		return siedler;
	}

	public Karte getKarte() {
		Swarm parent = getFather();
		if (parent != null && parent instanceof Insel) {
			return ((Insel) parent).karte;
		} else
			return null;
	}

	public Insel getInsel() {
		Swarm parent = getFather();
		if (parent != null && parent instanceof Insel) {
			return (Insel) parent;
		} else
			return null;
	}

	public void aufloesen() {
		for (Object kind : getChilds()) {
			if (kind != null && kind instanceof Siedler) {
				((Siedler) kind).sterben();
			}
		}
		leave();
	}

	public Siedler getRandomSiedler() {
		if (getChildCount() <= 1)
			return null;

		int siedlerId = getTop().getRandom().nextInt(getChildCount());
		Agent child = getChildAt(siedlerId);
		if (child instanceof Siedler)
			return (Siedler) child;
		else
			return null;
	}

	public int erhoeheHolz(int differenz) {
		synchronized(holzLock) {
			holz += differenz;
		}
		return holz;
	}

	public int verringereHolz(int differenz) {
		synchronized(holzLock) {
			holz -= differenz;
		}
		return holz;
	}

	public void setHolz(int holz) {
		synchronized(holzLock) {
			this.holz = holz;
		}
	}

	public int getHolz() {
		int holz;
		synchronized (holzLock) {
			holz = this.holz;
		}
		return holz;
	}

	public int erhoeheNahrung(int differenz) {
		synchronized(nahrungLock) {
			nahrung += differenz;
		}
		return nahrung;
	}

	public int verringereNahrung(int differenz) {
		synchronized(nahrungLock) {
			nahrung -= differenz;
		}
		return nahrung;
	}

	public void setNahrung(int nahrung) {
		synchronized(nahrungLock) {
			this.nahrung = nahrung;
		}
	}

	public synchronized int getNahrung() {
		int nahrung;
		synchronized(nahrungLock) {
			nahrung = this.nahrung;
		}
		return nahrung;
	}
}
