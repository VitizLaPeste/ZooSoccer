package project.controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.messages.ActionMessage;
import project.messages.JoinMessage;
import project.messages.ReplyActionMessage;
import project.messages.ReplyJoinMessage;
import project.model.Game;
import project.services.IGamesManagement;

@Controller
public class GameController
{
  @Autowired
  private IGamesManagement gamesManagement;
  
  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  static long[] limitLeft={0,500};
  static long[] limitRight={420,920};

  static long solHeight=800;
  static long limitJumpHeight=600;
  static long jumpVelocity=20;
  static double gravity= 1.2;
  
  @RequestMapping("game")
  public String game(Model model)
  {
    return "game";
  }
  
  @MessageMapping("game/join")
  public void join(SimpMessageHeaderAccessor headerAccessor, JoinMessage message)
  {
    System.out.println("join playerId : " + headerAccessor.getSessionId());
    
    long gameId = message.getGameId();
    
    ReplyJoinMessage reply = new ReplyJoinMessage();

    gamesManagement.createNewGame("a","b"); // en attendant
    Game game = gamesManagement.getGameById(gameId);
    
    if (game != null)
    {
      reply.setError(false);
      reply.setGameId(gameId);
    }
    else
    {
      reply.setError(true);
      reply.setErrorMessage("Can't find game with id " + gameId);
    }
    
    simpMessagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/game/replyjoin", reply);
  }

  @MessageMapping("game/move")
  public void depl(SimpMessageHeaderAccessor headerAccessor, ActionMessage message) {
    System.out.println("deplacement playerId : " + headerAccessor.getSessionId());
    String action = message.getAction();
    System.out.println("action depl : "+action);
    ReplyActionMessage reply = new ReplyActionMessage();
    long gameId= message.getGameId();
    Game game = gamesManagement.getGameById(gameId);
    if(game==null){
      reply.setError(true);
      reply.setErrorMessage("Can't find game with id " + gameId);
      simpMessagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/game/move", reply);
      return;
    }
    reply.setError(false);

    int j = 0;
    if(!game.isJ1(headerAccessor.getSessionId())){
      j=1;
    }
    switch(action){
      case "gauche":
        if(!gauche(j,game)) return;
        break;
      case "droite":
        if(!droite(j,game)) return;
        break;
      default:
        System.out.println("SALE TRICHEUR");
        return;
    }
    reply.setAllAttributesFromGame(game);
    reply.setIdJoueurInAction(headerAccessor.getSessionId());

    simpMessagingTemplate.convertAndSendToUser(game.getPlayersId()[0], "/game/move", reply);
    simpMessagingTemplate.convertAndSendToUser(game.getPlayersId()[1], "/game/move", reply);
    //simpMessagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/game/move", reply);
  }

  @MessageMapping("game/jump")
  public void jump(SimpMessageHeaderAccessor headerAccessor, ActionMessage message) {
    System.out.println("deplacement playerId : " + headerAccessor.getSessionId());
    String action = message.getAction();
    System.out.println("action jump : "+action);
    ReplyActionMessage reply = new ReplyActionMessage();
    long gameId= message.getGameId();
    Game game = gamesManagement.getGameById(gameId);
    if(game==null){
      reply.setError(true);
      reply.setErrorMessage("Can't find game with id " + gameId);
      simpMessagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/game/jump", reply);
      return;
    }
    reply.setError(false);
    int j = 0;
    if(!game.isJ1(headerAccessor.getSessionId())){
      j=1;
    }
    switch(action){
      case "saut":
        if(!saut(j,game)) return;
        break;
      case "enSaut":
        enSaut(j,game);
        break;
      default:
        System.out.println("SALE TRICHEUR");
        return;
    }
    reply.setAllAttributesFromGame(game);
    reply.setIdJoueurInAction(headerAccessor.getSessionId());

    System.out.println("VELOCITY YJ1 :  "+game.getVelocityYJ1());
    System.out.println("VELOCITY XJ1 :  "+game.getVelocityXJ1());
    System.out.println("VELOCITY YJ2 :  "+game.getVelocityYJ2());
    System.out.println("VELOCITY XJ2 :  "+game.getVelocityXJ2());
    simpMessagingTemplate.convertAndSendToUser(game.getPlayersId()[0], "/game/jump", reply);
    simpMessagingTemplate.convertAndSendToUser(game.getPlayersId()[1], "/game/jump", reply);
    //simpMessagingTemplate.convertAndSendToUser(headerAccessor.getSessionId(), "/game/jump", reply);
  }

  private boolean gauche(int j,Game game){
    long xJ;
    if(j==0){
      xJ=game.getxJ1();
      if(xJ-10<limitLeft[j]) return false;
      game.setxJ1(xJ-10);
      game.setVelocityXJ1(-jumpVelocity/2);
    }else{
      xJ=game.getxJ2();
      if(xJ-10<limitLeft[j]) return false;
      game.setxJ2(xJ-10);
      game.setVelocityXJ2(-jumpVelocity/2);
    }
    return true;
  }
  private boolean droite(int j,Game game){
    long xJ;
    if(j==0){
      xJ=game.getxJ1();
      if(xJ+10>limitRight[j]) return false;
      game.setxJ1(xJ+10);
      game.setVelocityXJ1(jumpVelocity/2);
    }else{
      xJ=game.getxJ2();
      if(xJ+10>limitRight[j]) return false;
      game.setxJ2(xJ+10);
      game.setVelocityXJ2(jumpVelocity/2);
    }
    return true;
  }
  private boolean saut(int j,Game game){
    long yJ;
    long xJ;
    if(j==0){
      yJ=game.getyJ1();
      xJ=game.getxJ1();
      if(yJ!=solHeight){ // deja en train de sauter/retomber
        System.out.println("saut rejeté car dans les airs");
        return false;
      }
      game.setVelocityYJ1(jumpVelocity);
      game.setyJ1(yJ-jumpVelocity); // on soustrait pour sauter car le repere est à l'envers
      if(game.getVelocityXJ1()!=0){
        long x = xJ+game.getVelocityXJ1();
        if(x<limitLeft[j]){
          x=limitLeft[j];
        }else if(x>limitRight[j]){
          x=limitRight[j];
        }
        game.setxJ1(x);
        game.setVelocityXJ1(0);
      }
    }else{
      yJ=game.getyJ2();
      xJ=game.getxJ2();
      if(yJ!=solHeight){ // deja en train de sauter/retomber
        System.out.println("saut rejeté car dans les airs");
        return false;
      }
      game.setVelocityYJ2(jumpVelocity);
      game.setyJ2(yJ-jumpVelocity); // on soustrait pour sauter car le repere est à l'envers
      if(game.getVelocityXJ2()!=0){
        long x = xJ+game.getVelocityXJ2();
        if(x<limitLeft[j]){
          x=limitLeft[j];
        }else if(x>limitRight[j]){
          x=limitRight[j];
        }
        game.setxJ2(x);
        game.setVelocityXJ2(0);
      }
    }
    return true;
  }
  private void enSaut(int j,Game game){
    long yJ;
    long xJ;
    if(j==0){
      yJ=game.getyJ1();
      xJ=game.getxJ1();
      //if(yJ>=solHeight) {return false;}
      if(yJ<=limitJumpHeight){
        game.setVelocityYJ1(-jumpVelocity);
      }else if(game.getVelocityYJ1()<0){
        game.setVelocityYJ1((long)Math.ceil(game.getVelocityYJ1()*gravity));
      }
      game.setyJ1(yJ-game.getVelocityYJ1());
      if(game.getyJ1()>=solHeight){ // pas <= car repere à l'envers
        game.setyJ1(solHeight);
        game.setVelocityYJ1(0);
      }
      if(game.getVelocityXJ1()!=0){
        long x = xJ+game.getVelocityXJ1();
        if(x<limitLeft[j]){
          x=limitLeft[j];
        }else if(x>limitRight[j]){
          x=limitRight[j];
        }
        game.setxJ1(x);
        game.setVelocityXJ1(0);
      }
    }else{
      yJ=game.getyJ2();
      xJ=game.getxJ2();
      if(yJ<=limitJumpHeight){
        game.setVelocityYJ2(-jumpVelocity);
      }else if(game.getVelocityYJ2()<0){
        game.setVelocityYJ2((long)Math.ceil(game.getVelocityYJ2()*gravity));
      }
      game.setyJ2(yJ-game.getVelocityYJ2());
      if(game.getyJ2()>=solHeight){ // pas <= car repere à l'envers
        game.setyJ2(solHeight);
        game.setVelocityYJ2(0);
      }
      if(game.getVelocityXJ2()!=0){
        long x = xJ+game.getVelocityXJ2();
        if(x<limitLeft[j]){
          x=limitLeft[j];
        }else if(x>limitRight[j]){
          x=limitRight[j];
        }
        game.setxJ2(x);
        game.setVelocityXJ2(0);
      }
    }
  }

  @MessageMapping("game/connected/{gameId}")
  public void connected(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable long gameId)
  {
    System.out.println("connected playerId : " + headerAccessor.getSessionId() + " gameId : " + gameId);
  }
}
