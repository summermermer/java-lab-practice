import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class Blackjack {

	public static void main(String[] args) {
		boolean isRun=true,Turn=true;
		Scanner scan=new Scanner(System.in);
		int seed=scan.nextInt();
		int playnum=scan.nextInt();
		ArrayList<Computer> players= new ArrayList<>();
		Deck deck=new Deck();
		deck.setCard();
		deck.shuffle(seed);
		
		Player player1=new Player();		//player1 생성
		player1.pynum=1;
		for(int i=2;i<=playnum;i++) {
			Computer com=new Computer();
			players.add(com);
			players.get(i-2).pynum=i;
		}									//나머지 computer players생성
		House dealer=new House();
		dealer.pynum=0;						//house 생성

		for(int i=0;i<2;i++) {
			player1.cards.add(deck.dealCard());
			for(int j=0;j<=playnum-2;j++) {
				players.get(j).cards.add(deck.dealCard());
			}
			dealer.cards.add(deck.dealCard());
		}
		//처음 카드 놓기 (2장씩)
		
		dealer.hidFaceup();
		player1.Faceup();
		for(int i=0;i<=playnum-2;i++) {
			players.get(i).Faceup();
		}
		
		
		if(dealer.sum>=21) isRun=false;
		else {
			System.out.println("\n--- Player1 turn ---");
			scan.nextLine();
			while(Turn) {
				player1.Faceup();
				if(player1.busted) break;

				
				String line=scan.nextLine();
				
				if(line.equals("hit")) {
					player1.cards.add(deck.dealCard());
					player1.Sum();
				}
				else if(line.equals("stand")) {
					player1.Faceup();
					Turn=false;
				}
			}
			for(int i=0;i<=playnum-2;i++) {
				Turn=true;
				System.out.println("\n--- Player"+Integer.toString(i+2)+" turn ---");
				Computer py=new Computer();
				py=players.get(i);
				while(Turn) {
					py.Faceup();
					if(py.busted) Turn=false;
					else {
						int is_hit=0;
						if(py.sum<14) is_hit=1;
						else if(py.sum>17) is_hit=0;
						else {
							Random random=new Random();
							is_hit=(int)(random.nextInt(2));
						}
						if(is_hit==1) {
							System.out.println("Hit");
							py.cards.add(deck.dealCard());
						}
						else {
							System.out.println("Stand");
							py.Faceup();
							Turn=false;
						}
					}
				}
			}
			System.out.println("\n--- House turn ---");
			dealer.Faceup();
			Turn=true;
			while(Turn) {
				if(dealer.busted) Turn=false;
				else {
					if(dealer.sum<=16) {
						System.out.println("Hit");
						dealer.cards.add(deck.dealCard());
					}
					else {
						System.out.println("Stand");
						Turn=false;
					}
					dealer.Faceup();
				}
			}
		}
		
		System.out.println("\n--- Game Results ---");
		dealer.Faceup();
		player1.Result(dealer.sum);
		for(int j=0;j<=playnum-2;j++) {
			players.get(j).Result(dealer.sum);
		}
		scan.close();
	}

}
class Card{
	public int value,suit;
	public Card() {}
	public Card(int theSuit, int theValue) {
		this.value=theValue;
		this.suit=theSuit;
	}
}
class Deck{
	private Card[] deck=new Card[52];
	private int cardsUsed;
	public void setCard() {
		int i,j,cnt=0,sut,val;
		for(i=1;i<=13;i++) {
			for(j=1;j<=4;j++) {
				if(j==1) sut='c';
				else if(j==2) sut='h';
				else if(j==3) sut='d';
				else sut='s';
				switch(i) {
					case 1: val='A'; break;
					case 11: val='J'; break;
					case 12: val='Q'; break;
					case 13: val='K'; break;
					default: val=i;
				}
				deck[cnt]=new Card(sut,val);
				cnt++;
			}
		}
	}	//52장의 카드 생성
	
	public void shuffle(int seed) {
		Random random=new Random(seed);
		for(int i=deck.length-1;i>0;i--) {
			int rand=(int) (random.nextInt(i+1));
			Card temp=deck[i];
			deck[i]=deck[rand];
			deck[rand]=temp;
		}
		cardsUsed=0;
	}
	public Card dealCard() {
		if(cardsUsed==deck.length)
			throw new IllegalStateException("No cards are left in the deck.");
		cardsUsed++;
		return deck[cardsUsed-1];
	}
}
class Hand{
	ArrayList <Card> cards=new ArrayList<>();
	
	public int pynum;
	public int sum=0;
	public boolean won=false,busted=false;

	public void Sum() {
		Iterator<Card> iterator=this.cards.iterator();
		sum = 0;
		int anum=0;
		while(iterator.hasNext()) {
			Card card=new Card();
			card=iterator.next();
			if(card.value>=65) {
				if(card.value=='A') {
					anum++;
					this.sum+=11;
				}
				else if(card.value=='J'||card.value=='Q'||card.value=='K') {
					this.sum+=10;
				}
			}
			else this.sum+=card.value;
		}
		if(this.sum>21) {
			if(anum>0) {
				this.sum-=(anum*10);
				if(this.sum>21) this.busted=true;
			}
			else this.busted=true;
		}
	}
	public Hand() {	}
}
class Computer extends Hand{
	public void Result(int house) {
		if(house>21) {
			if(this.busted) System.out.print("[Lose] ");
			else System.out.print("[Win] ");
		}
		else {
			if(this.sum>house&&this.sum<=21) System.out.print("[Win] ");
			else if(this.sum==house) System.out.print("[Draw] ");
			else System.out.print("[Lose] ");	
		}
		this.Faceup();
	}
	public void Faceup() {
		this.Sum();
		System.out.print("Player"+Integer.toString(this.pynum)+": ");
		Iterator<Card> iterator=this.cards.iterator();
		while(iterator.hasNext()) {
			Card card=new Card();
			card=iterator.next();
			if(card.value>=65) 	System.out.print((char)card.value);
			else	System.out.print(card.value);
			System.out.print((char)card.suit+" ");
		}
		if(this.sum>21) System.out.println("("+this.sum+")"+" - Bust!");
		else System.out.println("("+this.sum+")");
	}
	
}
class Player extends Hand{
	public void Result(int house) {
		if(house>21) {
			if(this.busted) System.out.print("[Lose] ");
			else System.out.print("[Win] ");
		}
		else {
			if(this.sum>house&&this.sum<=21) System.out.print("[Win] ");
			else if(this.sum==house) System.out.print("[Draw] ");
			else System.out.print("[Lose] ");	
		}
		this.Faceup();
	}
	public void Faceup() {
		this.Sum();
		System.out.print("Player1: ");
		Iterator<Card> iterator=this.cards.iterator();
		while(iterator.hasNext()) {
			Card card=new Card();
			card=iterator.next();
			if(card.value>=65) 	System.out.print((char)card.value);
			else	System.out.print(card.value);
			System.out.print((char)card.suit+" ");
		}
		if(this.busted) System.out.println("("+this.sum+")"+" - Bust!");
		else System.out.println("("+this.sum+")");
	}
	
}
class House extends Hand{
	public void hidFaceup() {
		this.Sum();
		System.out.print("House: ");
		Iterator<Card> iterator=this.cards.iterator();
		iterator.next();
		System.out.print("HIDDEN ");
		while(iterator.hasNext()) {
			Card card=new Card();
			card=iterator.next();
			if(card.value>=65) 	System.out.print((char)card.value);
			else	System.out.print(card.value);
			System.out.print((char)card.suit+" ");
		}
		System.out.println();
	}

	public void Faceup() {
		this.Sum();
		System.out.print("House: ");
		Iterator<Card> iterator=this.cards.iterator();
		while(iterator.hasNext()) {
			Card card=new Card();
			card=iterator.next();
			if(card.value>=65) 	System.out.print((char)card.value);
			else	System.out.print(card.value);
			System.out.print((char)card.suit+" ");
		}
		if(this.busted) System.out.println("("+this.sum+")"+" - Bust!");
		else System.out.println("("+this.sum+")");
	}
}