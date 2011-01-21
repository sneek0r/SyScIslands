package SyScIslands;

import eawag.model.Swarm;

public class Insel extends Swarm {
	private static int autoid = 0;
	private boolean init = true;

	private int curHolz = 0;
	private int curWild = 0;
	public boolean wasser = false;

	public int holzMax = 1;
	public int wildMax = 1;
	/**
	 * Zugaenglichkeit der Insel entscheidet ueber die Daeur der Berufsausuebung
	 * (0=kurz, 1=lang)
	 */
	public float zugaenglichkeit = 1f;

	/** Erlaubt den zugriff auf die hinterlegte Werte */
	protected Karte karte;

	protected Dorf dorf = null;

	/** Insel Id */
	int id;
	int groesse = 0;
	
	/** Synchronisations Objekte */
	private Object holzLock = new Object();
	private Object wildLock = new Object();

	public Insel(Karte karte) {
		this.id = autoid++;
		this.karte = karte;
		karte.inseln.add(this);
	}

	public int getID() {
		return id;
	}

	public int getGroesse() {
		return groesse;
	}

	public synchronized void setGroesse(int groesse) {
		this.groesse = groesse;
	}

	@Override
	public void condition() {
		super.condition();
		if (init) {
			return;
		}
		// Regenerationsphase
		regenerateResorces();
	}

	@Override
	public void action() {
		if (init) {
			if (karte == null)
				return;

			// Ressourcen Initalisierung
			java.util.Random rnd = getTop().getRandom();
			int groesse = getGroesse();
			this.holzMax = karte.holzMax * groesse;
			this.wildMax = karte.wildMax * groesse;
			this.curHolz = (karte.holzMin + rnd.nextInt(karte.holzMax
					- karte.holzMin))
					* groesse;
			this.curWild = (karte.wildMin + rnd.nextInt(karte.wildMax
					- karte.wildMin))
					* groesse;

			if (rnd.nextFloat() > karte.Wasserwahrscheinlichkeit)
				wasser = true;
			zugaenglichkeit = rnd.nextFloat();

			System.out.print("Holz: " + curHolz + " Wild: " + curWild + "\n");
			init = false;
			return;
		}
	}

	private void regenerateResorces() {

		if (curHolz + (karte.holzReg * getGroesse()) > holzMax)
			curHolz = holzMax;
		else
			curHolz += (karte.holzReg * getGroesse());

		if (curWild + (karte.wildReg * getGroesse()) > wildMax)
			curWild = wildMax;
		else
			curWild += (karte.wildReg * getGroesse());
	}

	public void setDorf(Dorf dorf) throws IllegalAccessException {
		if (this.dorf != null)
			throw new IllegalAccessException("Dorf schon vorhanden!");
		this.dorf = dorf;
		dorf.join(this);
	}

	public void entferneDorf() {
		dorf.aufloesen();
		dorf = null;
	}

	public Dorf getDorf() {
		return dorf;
	}
	
	public int getHolz() {
		int holz;
		synchronized(holzLock) {
			holz = curHolz;
		}
		return holz;
	}
	
	public int getWild() {
		int wild;
		synchronized(wildLock) {
			wild = curWild;
		}
		return wild;
	}
	
	public void setHolz(int holz) {
		synchronized(holzLock) {
			curHolz = holz;
		}
	}
	
	public void setWild(int wild) {
		synchronized(wildLock) {
			curWild = wild;
		}
	}
	
	public void erhoeheHolz(int differenz) {
		synchronized(holzLock) {
			curHolz += differenz;
		}
	}
	
	public void verringereHolz(int differenz) {
		synchronized(holzLock) {
			curHolz -= differenz;
		}
	}
	
	public void erhoeheWild(int differenz) {
		synchronized(wildLock) {
			curWild += differenz;
		}
	}
	
	public void verringereWild(int differenz) {
		synchronized(wildLock) {
			curWild -= differenz;
		}
	}
}
