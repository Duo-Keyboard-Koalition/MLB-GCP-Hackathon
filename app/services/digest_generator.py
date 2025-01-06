from app.models import User, Team, Player
import google.generativeai as genai
from config import Config

def generate_digest(user):
    """
    Generate a personalized digest for the user based on their followed teams and players
    """
    # Initialize Gemini
    genai.configure(api_key=Config.GEMINI_API_KEY)
    model = genai.GenerativeModel('gemini-pro')
    
    # Gather information about user's followed teams and players
    teams_info = [team.name for team in user.teams]
    players_info = [player.name for player in user.players]
    
    # Create prompt for Gemini
    prompt = f"""Create a sports digest summary for a fan who follows:
    Teams: {', '.join(teams_info)}
    Players: {', '.join(players_info)}
    
    Include recent game results, player statistics, and notable news.
    Keep it concise but informative."""
    
    # Generate content using Gemini
    response = model.generate_content(prompt)
    
    return response.text
