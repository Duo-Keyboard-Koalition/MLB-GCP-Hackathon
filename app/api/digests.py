from flask import jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.api import bp
from app.models import User, Digest
from app.services.digest_generator import generate_digest
from app.services.translator import translate_text
from app import db

@bp.route('/digests/latest', methods=['GET'])
@jwt_required()
def get_latest_digest():
    user_id = get_jwt_identity()
    user = User.query.get_or_404(user_id)
    
    digest = Digest.query.filter_by(
        user_id=user_id,
        language=user.preferred_language
    ).order_by(Digest.created_at.desc()).first()
    
    if not digest:
        return jsonify({'error': 'No digest available'}), 404
    
    return jsonify({
        'content': digest.content,
        'content_type': digest.content_type,
        'created_at': digest.created_at
    })

@bp.route('/digests/generate', methods=['POST'])
@jwt_required()
def create_digest():
    user_id = get_jwt_identity()
    user = User.query.get_or_404(user_id)
    
    # Generate digest content based on user preferences
    content = generate_digest(user)
    
    # Translate content if needed
    if user.preferred_language != 'en':
        content = translate_text(content, 'en', user.preferred_language)
    
    digest = Digest(
        user_id=user_id,
        content=content,
        content_type='text',  # Default to text, can be extended for audio/video
        language=user.preferred_language
    )
    
    db.session.add(digest)
    db.session.commit()
    
    return jsonify({'message': 'Digest generated successfully'})
