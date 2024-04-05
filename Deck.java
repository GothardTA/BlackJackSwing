import java.util.List;
import java.util.ArrayList;

/**
 * The Deck class represents a shuffled deck of cards.
 * It provides several operations including
 *      initialize, shuffle, deal, and check if empty.
 */
public class Deck implements Cloneable {

	/**
	 * cards contains all the cards in the deck.
	 */
	private ArrayList<Card> cards;

	/**
	 * size is the number of not-yet-dealt cards.
	 * Cards are dealt from the top (highest index) down.
	 * The next card to be dealt is at size - 1.
	 */
	private int size;


	/**
	 * Creates a new <code>Deck</code> instance.<BR>
	 * It pairs each element of ranks with each element of suits,
	 * and produces one of the corresponding card.
	 * @param ranks is an array containing all of the card ranks.
	 * @param suits is an array containing all of the card suits.
	 * @param values is an array containing all of the card point values.
	 */
	public Deck(String[] ranks, String[] suits, int[] values) {
		cards = new ArrayList<Card>();

		for (int suitN = 0; suitN < suits.length; suitN++) {
			for (int i = 0; i < ranks.length; i++) {
				String rank = ranks[i];
				String suit = suits[suitN];
				int value = values[i];
				Card card = new Card(rank, suit, value);
				cards.add(card);
			}
		}

		this.size = cards.size();

		shuffle();
	}


	/**
	 * Determines if this deck is empty (no undealt cards).
	 * @return true if this deck is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Accesses the number of undealt cards in this deck.
	 * @return the number of undealt cards in this deck.
	 */
	public int size() {
		return size;
	}

	/**
	 * Randomly permute the given collection of cards
	 * and reset the size to represent the entire deck.
	 */
	public void shuffle() {
		int shuffleAmount = (int)(Math.random()*20) + 3;
        for (int notUsed = 0; notUsed < shuffleAmount; notUsed++) { // a random amount
            // create new arrays
            int half = cards.size() / 2;
            Card[] first = new Card[half];
            Card[] second = new Card[half];

            // split the deck
            for (int i = 0; i < half; i++) {
                first[i] = cards.get(i);
            }
            for (int i = half; i < cards.size(); i++) {
                second[i-half] = cards.get(i);
            }

			int rng = (int)(Math.random() * 2);
			if (rng == 0) {
				Card[] tmp = first;
				first = second;
				second = tmp;
			}

			ArrayList<Card> tmp = new ArrayList<Card>();

            // shuffle
            for (int i = 0; i < half; i++) {
                for (int j = 0; j < 2; j++) {
                    if (j == 0) { tmp.add(first[i]); }
                    else { tmp.add(second[i]); }
                }
            }

			cards = tmp;
        }

		this.size = cards.size();
	}

	/**
	 * Deals a card from this deck.
	 * @return the card just dealt, or null if all the cards have been
	 *         previously dealt.
	 */
	public Card deal() {
		size--;
		if (size < 0) {
			return null;
		}
		return cards.get(size);
	}

	public Card cardAt(int i) {
		return cards.get(i);
	}

	/**
	 * Generates and returns a string representation of this deck.
	 * @return a string representation of this deck.
	 */
	@Override
	public String toString() {
		String rtn = "size = " + size + "\nUndealt cards: \n";

		for (int k = size - 1; k >= 0; k--) {
			rtn = rtn + cards.get(k);
			if (k != 0) {
				rtn = rtn + ",   ";
			}
			if ((size - k) % 2 == 0) {
				// Insert carriage returns so entire deck is visible on console.
				rtn = rtn + "\n";
			}
		}

		rtn = rtn + "\nDealt cards: \n";
		for (int k = cards.size() - 1; k >= size; k--) {
			rtn = rtn + cards.get(k);
			if (k != size) {
				rtn = rtn + ",   ";
			}
			if ((k - cards.size()) % 2 == 0) {
				// Insert carriage returns so entire deck is visible on console.
				rtn = rtn + "\n";
			}
		}

		rtn = rtn + "\n";
		return rtn;
	}
}