import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;


//represent a card
class Card {
  int rank;
  String suit;
  Posn posn;
  Color color;
  boolean faceUp;


  Card(int rank, String suit, Posn posn, Color color, boolean faceUp) {
    this.rank = rank;
    this.suit = suit;
    this.posn = posn;
    this.color = color;
    this.faceUp = faceUp;
  }

  //draw the image of the card
  WorldImage drawCard() {
    RectangleImage empty = new RectangleImage(40, 60, OutlineMode.OUTLINE, Color.BLACK);
    RectangleImage faceDown = new RectangleImage(40, 60, OutlineMode.SOLID, Color.BLACK);
    if (faceUp) {
      return new OverlayOffsetImage((new TextImage(
          this.suit, 15, FontStyle.BOLD, this.color)), 15, 20,
          new OverlayImage(new TextImage(
              this.rankToString(), 15, FontStyle.BOLD, this.color), empty));
    } else {
      return faceDown;
    }
  }

  //returns the String according to card's rank
  String rankToString() {
    if (this.rank == 1) {
      return "A";
    } 
    else if (this.rank == 11) {
      return "J";
    } 
    else if (this.rank == 12) {
      return "Q";
    } 
    else if (this.rank == 13) {
      return "K";
    } 
    else {
      return Integer.toString(this.rank);
    }
  }

  //Convert the suit to the according value
  int suitToInt() {
    if (this.suit.equals("♦")) {
      return 1;
    } 
    else if (this.suit.equals("♣")) {
      return 2;
    } 
    else if (this.suit.equals("♥")) {
      return 3;
    }
    else {
      return 4;
    }
  }

  //Effect: change the card's position to the given new position
  void updatePosn(Posn newPosn) {
    this.posn = newPosn;
  }
}

//represent the game Concentration
class Concentration extends World {
  ArrayList<Card> cards;
  int score;
  int time;
  ArrayList<Card> cardsFaceUp;
  int stepsLeft;


  Concentration(ArrayList<Card> cards) {
    this.cards = cards;
    this.score = 26;
    this.time = 0;
    this.cardsFaceUp = new ArrayList<Card>();
    this.stepsLeft = 80;
  }

  //Cards before shuffle
  void initData() {
    cards = new ArrayList<Card>();
    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♦", new Posn(30 + 50 * i, 70), Color.red, false));
    }

    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♣", new Posn(30 + 50 * i, 140), Color.black, false));
    }

    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♥", new Posn(30 + 50 * i, 210), Color.red, false));
    }

    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♠", new Posn(30 + 50 * i, 280), Color.black, false));
    }

  }


  //return the current world scene
  public WorldScene makeScene() {
    WorldScene empty = new WorldScene(660, 350);
    WorldImage score = new TextImage("Score: ".concat(
        Integer.toString(this.score)), 15, FontStyle.BOLD, Color.black);
    WorldImage time = new TextImage("Time spent: ".concat(
        Integer.toString(this.time)).concat(" secs"), 15, FontStyle.BOLD, Color.black);
    WorldImage stepsLeft = new TextImage("Steps Left: ".concat(
        Integer.toString(this.stepsLeft)), 15, FontStyle.BOLD, Color.black);

    for (Card c : cards) {
      empty.placeImageXY(c.drawCard(), c.posn.x, c.posn.y);
    }
    empty.placeImageXY(score, 50, 20);
    empty.placeImageXY(time, 230, 20);
    empty.placeImageXY(stepsLeft, 450, 20);


    return empty;
  }

  //Effect: let the face down card be face up
  public void onMouseClicked(Posn pos) {

    if (cardsFaceUp.size() < 2) {
      for (Card c : cards) {
        if (c.faceUp
            && pos.x <= c.posn.x + 20
            && pos.x >= c.posn.x - 20
            && pos.y <= c.posn.y + 30
            && pos.y >= c.posn.y - 30) {
          c.faceUp = true;
          cardsFaceUp.add(c);
          stepsLeft --;
        }
      }
    }  
  }

  //Effect: count the time spent and the score. Remove twos face up cards if they have
  //the same color and value. Make the face up cards face down if they don't have the same
  //color or same value.
  public void onTick() {
    this.time = this.time + 1;
    if (cardsFaceUp.size() == 2) {
      if (cardsFaceUp.get(0).rank == cardsFaceUp.get(1).rank
          && cardsFaceUp.get(0).color == cardsFaceUp.get(1).color) {
        cards.remove(cards.indexOf(cardsFaceUp.get(0)));
        cards.remove(cards.indexOf(cardsFaceUp.get(1)));
        cardsFaceUp = new ArrayList<Card>(); 
        score = score - 1;
      }
      else {
        cards.get(cards.indexOf(cardsFaceUp.get(0))).faceUp = false;
        cards.get(cards.indexOf(cardsFaceUp.get(1))).faceUp = false;
        cardsFaceUp = new ArrayList<Card>(); 
      }
    }
  }

  //Effect: press the key "r" to restart the game
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      initData();
      Util util = new Util();

      this.cards = util.shuffle(cards);
      this.score = 26;
      this.time = 0;
      this.cardsFaceUp = new ArrayList<Card>();

    }
  }

  // the last scene of the game
  public WorldScene lastScene(String msg) {
    WorldImage lose = new TextImage("You Lose", 24, FontStyle.BOLD, Color.RED);
    WorldScene empty = new WorldScene(660, 350);
    WorldImage win = new TextImage("You Win", 24, FontStyle.BOLD, Color.red);
    if (msg.equals("You Lose")) {
      empty.placeImageXY(lose, 150, 200); 
    }
    if (msg.equals("You Win")) {
      empty.placeImageXY(win, 150, 200);
    }
    else {
      this.makeScene();
    }
    return empty;
  }


  //end the game
  public WorldEnd worldEnds() {
    if (cards.size() == 0) {
      return new WorldEnd(true, this.lastScene("You Win"));
    }
    else if (stepsLeft == 0) {
      return new WorldEnd(true, this.lastScene("You Lose"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }


}

class Util {
  Util() {}


  // Shuffle all the card to approach randomize
  ArrayList<Card> shuffle(ArrayList<Card> cards) {
    ArrayList<Posn> listOfPosn = new ArrayList<Posn>();
    for (int i = 0; i < cards.size(); i ++) {
      listOfPosn.add(cards.get(i).posn);
    }

    Random rand = new Random();

    for (int i = 0; i < cards.size(); i ++) {
      int index = rand.nextInt(listOfPosn.size());
      cards.get(i).updatePosn(listOfPosn.get(index));
      listOfPosn.remove(index);
    }
    return cards;
  }
}

class CompareCard implements Comparator<Card> {

  // compare given card if the suit is same, compare rank, or compare suitValue;
  public int compare(Card c1, Card c2) {
    if (c1.suit.equals(c2.suit)) {
      return c1.rank - c2.rank;
    }
    else {
      return c1.suitToInt() - c2.suitToInt();
    }
  }
}

class ExamplesConcentration {
  ArrayList<Card> cards;
  Card card1;
  Card card2;
  Card card3;
  Card card4;
  ArrayList<Card> cardsTest;
  Concentration worldTest;

  // Initialize the data in examplesConcentration
  void initData() {
    cards = new ArrayList<Card>();
    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♦", new Posn(30 + 50 * i, 70), Color.red, false));
    }

    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♣", new Posn(30 + 50 * i, 140), Color.black, false));
    }

    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♥", new Posn(30 + 50 * i, 210), Color.red, false));
    }

    for (int i = 0; i < 13; i ++) {
      cards.add(new Card(i + 1, "♠", new Posn(30 + 50 * i, 280), Color.black, false));
    }


    //    cards = util.shuffle(cards);
  }

  // Initialize the data for test
  void initTest() {
    card1 = new Card(11, "♦", new Posn(30, 70), Color.red,true);
    card2 = new Card(3, "♣", new Posn(30, 140), Color.black, false);
    card3 = new Card(1, "♥", new Posn(30, 210), Color.red, false);
    card4 = new Card(2, "♠", new Posn(30, 280), Color.black, false);

    cardsTest = new ArrayList<Card>(Arrays.asList(card1, card2, card3, card4));

    worldTest = new Concentration(this.cardsTest);
  }

  // Test the BigBang
  void testBigBang(Tester t) {
    initData();
    Concentration world = new Concentration(this.cards);
    int worldWidth = 660;
    int worldHeight = 350;
    int tickRate = 1;

    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  // Test the method for makeScene
  void testMakeScene(Tester t) {
    initTest();
    WorldScene empty = new WorldScene(660, 350); 

    WorldImage score = new TextImage("Score: ".concat(
        Integer.toString(worldTest.score)), 15, FontStyle.BOLD, Color.black);
    WorldImage time = new TextImage("Time spent: ".concat(
        Integer.toString(worldTest.time)).concat(" secs"), 15, FontStyle.BOLD, Color.black);
    WorldImage stepsLeft = new TextImage("Steps Left: ".concat(
        Integer.toString(worldTest.stepsLeft)), 15, FontStyle.BOLD, Color.black);

    empty.placeImageXY(card1.drawCard(), 30, 70);
    empty.placeImageXY(card2.drawCard(), 30, 140);
    empty.placeImageXY(card3.drawCard(), 30, 210);
    empty.placeImageXY(card4.drawCard(), 30, 280);
    empty.placeImageXY(score, 50, 20);
    empty.placeImageXY(time, 230, 20);
    empty.placeImageXY(stepsLeft, 450, 20);

    t.checkExpect(worldTest.makeScene(), empty);
  }

  // Test the method for drawCard
  void testDrawCard(Tester t) {
    Card card1 = new Card(1, "♦", new Posn(30, 70), Color.red, true);

    Card card2 = new Card(1, "♦", new Posn(30, 70), Color.red, false);

    Card card3 = new Card(2, "♦", new Posn(30, 70), Color.red, true);

    t.checkExpect(card1.drawCard(), 
        new OverlayOffsetImage((new TextImage("♦", 15, FontStyle.BOLD, Color.red)), 15, 20,
            new OverlayImage(new TextImage("A", 15, FontStyle.BOLD, Color.red), 
                new RectangleImage(40, 60, OutlineMode.OUTLINE, Color.BLACK))));

    t.checkExpect(card2.drawCard(),
        new RectangleImage(40, 60, OutlineMode.SOLID, Color.BLACK));

    t.checkExpect(card3.drawCard(), 
        new OverlayOffsetImage((new TextImage("♦", 15, FontStyle.BOLD, Color.red)), 15, 20,
            new OverlayImage(new TextImage("2", 15, FontStyle.BOLD, Color.red), 
                new RectangleImage(40, 60, OutlineMode.OUTLINE, Color.BLACK))));
  }

  // test the method for rankToString
  void testRankToString(Tester t) {
    Card card1 = new Card(1, "♦", new Posn(30, 70), Color.red, true);
    Card card2 = new Card(2, "♦", new Posn(30, 70), Color.red, true);
    Card card3 = new Card(11, "♦", new Posn(30, 70), Color.red, true);
    Card card4 = new Card(12, "♦", new Posn(30, 70), Color.red, true);
    Card card5 = new Card(13, "♦", new Posn(30, 70), Color.red, true);
    t.checkExpect(card1.rankToString(), "A");
    t.checkExpect(card2.rankToString(), "2");
    t.checkExpect(card3.rankToString(), "J");
    t.checkExpect(card4.rankToString(), "Q");
    t.checkExpect(card5.rankToString(), "K");
  }

  // test the method updatePosn
  void testUpdatePosn(Tester t) {
    Card card1 = new Card(1, "♦", new Posn(30, 70), Color.red, true);
    t.checkExpect(card1.posn, new Posn(30, 70));
    card1.updatePosn(new Posn(80, 140));
    t.checkExpect(card1.posn, new Posn(80, 140));
  }

  // test the method randomList
  void testRandomList(Tester t) {
    Card card1 = new Card(1, "♦", new Posn(30, 70), Color.red, true);
    Card card2 = new Card(2, "♣", new Posn(30, 140), Color.black, true);
    Card card3 = new Card(11, "♥", new Posn(30, 210), Color.red, true);
    Card card4 = new Card(12, "♠", new Posn(30, 280), Color.black, true);

    ArrayList<Card> cardTest = new ArrayList<Card>(Arrays.asList(card1, card2, card3, card4));

    Util util = new Util();
    ArrayList<Card> expected = util.shuffle(cardTest);
    t.checkExpect(expected.size(), 4);
    Collections.sort(expected, new CompareCard());
    t.checkExpect(cardTest, expected);

  }

  // test the method for compareCard
  void testCompareCard(Tester t) {
    Card card1 = new Card(1, "♦", new Posn(30, 70), Color.red, true);
    Card card3 = new Card(3, "♣", new Posn(30, 140), Color.black, true);
    Card card4 = new Card(4, "♣", new Posn(30, 140), Color.black, true);
    Card card5 = new Card(1, "♥", new Posn(30, 210), Color.red, true);
    Card card7 = new Card(12, "♠", new Posn(30, 280), Color.black, true);

    CompareCard compareCard = new CompareCard();

    t.checkExpect(compareCard.compare(card1, card3), -1);
    t.checkExpect(compareCard.compare(card3, card4), -1);
    t.checkExpect(compareCard.compare(card5, card1), 2);
    t.checkExpect(compareCard.compare(card7, card3), 2);
  }

  // test the method for onMouseClicked
  void testOnMouseClicked(Tester t) {
    initData();
    Concentration world1 = new Concentration(this.cards);
    t.checkExpect(world1.cardsFaceUp, new ArrayList<Card>());
    t.checkExpect(world1.stepsLeft, 80);
    t.checkExpect(world1.cards.get(0).faceUp, false);
    world1.onMouseClicked(new Posn(30,70));
    t.checkExpect(world1.cardsFaceUp, 
        new ArrayList<Card>(Arrays.asList(cards.get(0))));
    t.checkExpect(world1.stepsLeft, 79);
    t.checkExpect(world1.cards.get(0).faceUp, true);

    t.checkExpect(world1.cardsFaceUp, 
        new ArrayList<Card>(Arrays.asList(cards.get(0))));
    t.checkExpect(world1.stepsLeft, 79);
    t.checkExpect(world1.cards.get(1).faceUp, false);
    world1.onMouseClicked(new Posn(80,70));
    t.checkExpect(world1.cardsFaceUp, 
        new ArrayList<Card>(Arrays.asList(cards.get(0), cards.get(1))));
    t.checkExpect(world1.stepsLeft, 78);
    t.checkExpect(world1.cards.get(1).faceUp, true);

    t.checkExpect(world1.cardsFaceUp, 
        new ArrayList<Card>(Arrays.asList(cards.get(0), cards.get(1))));
    t.checkExpect(world1.stepsLeft, 78);
    t.checkExpect(world1.cards.get(2).faceUp, false);
    world1.onMouseClicked(new Posn(130,70));
    t.checkExpect(world1.cardsFaceUp, 
        new ArrayList<Card>(Arrays.asList(cards.get(0), cards.get(1))));
    t.checkExpect(world1.stepsLeft, 78);
    t.checkExpect(world1.cards.get(2).faceUp, false);
  }

  // test the method for onTick
  void testOnTick(Tester t) {
    initData();
    Concentration world1 = new Concentration(this.cards);
    t.checkExpect(world1.time, 0);
    world1.onTick();
    t.checkExpect(world1.time, 1);
    
    Card card1 = new Card(1, "♦", new Posn(30, 70), Color.red, false);
    Card card2 = new Card(1, "♥", new Posn(30, 140), Color.red, false);
    Concentration world2 = new Concentration(new ArrayList<Card>(Arrays.asList(card1, card2)));
    t.checkExpect(world2.cards.size(), 2);
    t.checkExpect(world2.score, 26);
    world2.onMouseClicked(new Posn(30,70));
    world2.onMouseClicked(new Posn(30,140));
    t.checkExpect(world2.cards.size(), 2);
    t.checkExpect(world2.cardsFaceUp, new ArrayList<Card>(Arrays.asList(card1, card2)));
    world2.onTick();
    t.checkExpect(world2.cards.size(), 0);
    t.checkExpect(world2.score, 25);
    t.checkExpect(world2.cardsFaceUp, new ArrayList<Card>());
    
    Card card3 = new Card(1, "♦", new Posn(30, 70), Color.red, false);
    Card card4 = new Card(1, "♣", new Posn(30, 140), Color.black, false);
    Concentration world3 = new Concentration(new ArrayList<Card>(Arrays.asList(card3, card4)));
    t.checkExpect(world3.cards.size(), 2);
    t.checkExpect(world3.score, 26);
    world3.onMouseClicked(new Posn(30,70));
    world3.onMouseClicked(new Posn(30,140));
    t.checkExpect(world3.cards.size(), 2);
    t.checkExpect(world3.cardsFaceUp, new ArrayList<Card>(Arrays.asList(card3, card4)));
    world3.onTick();
    t.checkExpect(world3.cards.size(), 2);
    t.checkExpect(world3.score, 26);
    t.checkExpect(world3.cardsFaceUp, new ArrayList<Card>());
  }

  // test the method for OnKeyEvent
  void testOnKeyEvent(Tester t) {
    initData();
    ArrayList<Card> CardCopy = cards;
    Concentration world = new Concentration(this.cards);
    world.onKeyEvent("r");
    t.checkExpect(world.score, 26);
    t.checkExpect(world.stepsLeft, 80);
    t.checkExpect(world.time, 0);
    t.checkExpect(world.cards.size(), 52);
    Collections.sort(cards, new CompareCard());
    t.checkExpect(cards, CardCopy);
  }

  // test the method worldEnd for the situation when win
  void testWorldEndsForWin(Tester t) {
    initData();
    Concentration game = new Concentration(cards);
    for (int i = 0; i < cards.size(); i++ ) {
      cards.remove(i);
      i--;
    }
    t.checkExpect(game.worldEnds(), new WorldEnd(true, game.lastScene("You Win")));
  }

  //test the method worldEnd for the situation when lose
  void testWorldEndsForLose(Tester t) {
    initData();
    Concentration game = new Concentration(cards);
    game.stepsLeft = 0;
    t.checkExpect(game.worldEnds(), new WorldEnd(true, game.lastScene("You Lose")));
  }
  
  // test the method for lastScene
  void testLastScene(Tester t) {
    initData();
    WorldImage lose = new TextImage("You Lose", 24, FontStyle.BOLD, Color.RED);
    WorldScene empty = new WorldScene(660, 350);
    WorldScene empty2 = new WorldScene(660, 350);
    WorldImage win = new TextImage("You Win", 24, FontStyle.BOLD, Color.red);
    Concentration game = new Concentration(cards);
    empty.placeImageXY(win, 150, 200);
    empty2.placeImageXY(lose, 150, 200);
    t.checkExpect(game.lastScene("You Win"), empty);
    t.checkExpect(game.lastScene("You Lose"), empty2);
  }

}