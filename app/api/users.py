from flask import jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.api import bp
from app.models import User, Team, Player
from app import db

@bp.route('/users/preferences', methods=['PUT'])
@jwt_required()
def update_preferences():
    user_id = get_jwt_identity()
    user = User.query.get_or_404(user_id)
    data = request.get_json()
    
    if 'preferred_language' in data:
        user.preferred_language = data['preferred_language']
    if 'digest_frequency' in data:
        user.digest_frequency = data['digest_frequency']
    
    db.session.commit()
    return jsonify({'message': 'Preferences updated successfully'})

@bp.route('/users/teams', methods=['POST'])
@jwt_required()
def follow_team():
    user_id = get_jwt_identity()
    user = User.query.get_or_404(user_id)
    data = request.get_json()
    
    team = Team.query.get_or_404(data['team_id'])
    if team not in user.teams:
        user.teams.append(team)
        db.session.commit()
    
    return jsonify({'message': 'Team followed successfully'})

@bp.route('/users/players', methods=['POST'])
@jwt_required()
def follow_player():
    user_id = get_jwt_identity()
    user = User.query.get_or_404(user_id)
    data = request.get_json()
    
    player = Player.query.get_or_404(data['player_id'])
    if player not in user.players:
        user.players.append(player)
        db.session.commit()
    
    return jsonify({'message': 'Player followed successfully'})
