import google.generativeai as genai
from config import Config

def translate_text(text, source_lang, target_lang):
    """
    Translate text using Gemini API
    """
    genai.configure(api_key=Config.GEMINI_API_KEY)
    model = genai.GenerativeModel('gemini-pro')
    
    prompt = f"""Translate the following text from {source_lang} to {target_lang}:
    
    {text}
    
    Provide only the translated text in response."""
    
    response = model.generate_content(prompt)
    return response.text
