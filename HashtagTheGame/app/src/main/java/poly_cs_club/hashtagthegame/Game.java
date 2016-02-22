package poly_cs_club.hashtagthegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Game extends Activity {

    private Board m_Board;
    private Player m_PlayerOne;
    private Player m_PlayerTwo;
    private boolean m_Turn; // true player one, false player two.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        String gameType = intent.getStringExtra("gameType");

        m_Board = new Board();
        m_PlayerOne = new Player("PlayerOne", 0); //Always Human

        switch(gameType){
            case("single"):
                m_PlayerTwo = new Player("PlayerTwo", 1); //P2 is a Bot
            case("local"):
                m_PlayerTwo = new Player("PlayerTwo", 0); //P2 is local human
            case("online"):
                m_PlayerTwo = new Player("PlayerTwo", 2); //P2 is online human
        }

        m_Turn = false;
        setContentView(R.layout.activity_game);
    }

    public void takeTurn(Player player,Location selectedTile)
    {
        if(!m_Board.IsTileFull(selectedTile))
        {
            m_Board.setTileOwner(player, selectedTile);
            m_Turn = !m_Turn;
            if(ifFieldIsTaken(selectedTile))
            {
                m_Board.setFieldOwner(player, selectedTile);
                //update Field
                if(ifGameWon())
                {
                    //update Game Screen
                }
            }
        }else
        {
            //send message that tile is taken...
        }
    }
    private boolean ifFieldIsTaken(Location loc)
    {
       return m_Board.isFieldFull(loc);
    }
    private boolean ifGameWon()
    {
        return m_Board.isGameWon(m_PlayerOne.getScore(), m_PlayerTwo.getScore());
    }

    public void onTileClick(View view) {
        String idString = view.getTag().toString();
        String[] locationValues = idString.split(",");
        Integer[] locationIntValues = new Integer[locationValues.length];
        for (int i = 0; i < locationValues.length;i++){
            locationIntValues[i] = Integer.parseInt(locationValues[i]);
        }

        //Location of the clicked tile
        Location location = new Location(locationIntValues[0],locationIntValues[1],locationIntValues[2],locationIntValues[3]);

    }
}
