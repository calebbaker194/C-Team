package FunWebsite;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

public class Launcher {
	
	public Key key;
	
	public Launcher()
	{
		key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
		Spark.port(80);
		Spark.staticFiles.location("/"); // Static files
		Spark.get("/hello", (req,res) -> helloResponse(req,res) );
		Spark.get("/login", (req,res) -> loginResponse(req,res));
		Spark.post("/login", (req,res) -> loginPostResponse(req,res));
		Spark.get("/java-script-fun", (req,res) -> jsTutorial(req,res));
		Spark.get("/velocity", (req,res) -> velocityRender(req,res));
	}
	
	public Boolean verifyUserToken(String token)
	{
		System.out.println("verifying "+ token);
		if(token==null)
			return false;
		
		try
		{
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject().equals("confines");
		}
		catch(JwtException e) {e.printStackTrace();
		return false;}
	}
	
	public String getUserToken(String username)
	{
		return Jwts.builder()
				.setSubject(username)
				.signWith(key)
				.compact();
	}
	
	public String velocityRender(Request req, Response res) {
	    Map<String, Object> model = new HashMap<>();
	    
	    model.put("Name", "Confines");
	    model.put("Students", 15);
	    
	    HashMap<String,Integer> stock = new HashMap<String,Integer>();
	 
	    stock.put("candles", 10);
	    stock.put("roses", 5);
	    stock.put("plates", 8);
	    
	    model.put("stock", stock);
	    return new VelocityTemplateEngine().render(
	        new ModelAndView(model, "html/velocity.html")
	    );
	}

	public String jsTutorial(Request req, Response res) {
	    Map<String, Object> model = new HashMap<>();
	    return new VelocityTemplateEngine().render(
	        new ModelAndView(model, "html/js-tutorial.html")
	    );
	}

	public String loginPostResponse(Request req, Response res) {
	    String username = req.queryParams("uname");
	    String correctPW = BCrypt.hashpw("noescape", BCrypt.gensalt(12));
	    String password = req.queryParams("psw");
	    if(username.equals("confines") && BCrypt.checkpw(password, correctPW) )
	    {
	        res.cookie("confines-login-token", getUserToken(username));
	    	return "Success";
	    }

	    return loginResponse(req,res);
	}
	
	public String loginResponse(Request req, Response res) {
		System.out.println("login");
		if(verifyUserToken(req.cookie("confines-login-token")))
			return "success token login";
			
	    Map<String, Object> model = new HashMap<>();
	    return new VelocityTemplateEngine().render(
	        new ModelAndView(model, "html/login.html")
	    );
	}

	public String helloResponse(Request req,Response res)
	{
		return "login.html";
	}
	
	public static void main(String args[])
	{
		
		new Launcher();
	}
}