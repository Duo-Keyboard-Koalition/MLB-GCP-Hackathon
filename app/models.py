from datetime import datetime
from app import db

# Association tables for many-to-many relationships
user_teams = db.Table('user_teams',
    db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
    db.Column('team_id', db.Integer, db.ForeignKey('team.id'))
)

user_players = db.Table('user_players',
    db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
    db.Column('player_id', db.Integer, db.ForeignKey('player.id'))
)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(64), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password_hash = db.Column(db.String(128))
    preferred_language = db.Column(db.String(2), default='en')  # en, es, ja
    digest_frequency = db.Column(db.String(20), default='daily')  # daily, weekly
    teams = db.relationship('Team', secondary=user_teams, backref='followers')
    players = db.relationship('Player', secondary=user_players, backref='followers')
    
class Team(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), unique=True, nullable=False)
    sport = db.Column(db.String(64))
    players = db.relationship('Player', backref='team')

class Player(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), nullable=False)
    team_id = db.Column(db.Integer, db.ForeignKey('team.id'))
    position = db.Column(db.String(64))

class Digest(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    content_type = db.Column(db.String(20))  # text, audio, video
    content = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    language = db.Column(db.String(2))
