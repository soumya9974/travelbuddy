import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import { Card, CardBody, CardHeader } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { User, Mail, Edit2, Check, X, Shield, CheckCircle } from 'lucide-react';

export const Profile = () => {
  const { user, token, logout, setUser } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    username: user?.username || '',
    email: user?.email || '',
    bio: user?.bio || '',
    interests: user?.interests || '',
    destinations: user?.destinations || '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSave = async () => {
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const updatedUser = await apiRequest('/users/profile', {
        method: 'PUT',
        body: JSON.stringify(formData),
        token: token!,
      });

      setUser(updatedUser);
      setSuccess('Profile updated successfully!');
      setIsEditing(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      username: user?.username || '',
      email: user?.email || '',
      bio: user?.bio || '',
      interests: user?.interests || '',
      destinations: user?.destinations || '',
    });
    setIsEditing(false);
    setError('');
    setSuccess('');
  };

  return (
    <div className="max-w-2xl mx-auto min-h-screen pb-12">
      <div className="bg-gradient-to-r from-blue-600 via-cyan-600 to-teal-600 rounded-2xl p-8 mb-8 shadow-xl">
        <h1 className="text-3xl md:text-4xl font-bold text-white">Profile Settings</h1>
        <p className="text-blue-50 text-lg mt-2">Manage your account and travel preferences</p>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <h2 className="text-xl font-semibold text-gray-900">Personal Information</h2>
              {user?.verified && (
                <span className="flex items-center gap-1 px-2 py-1 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full">
                  <CheckCircle size={14} />
                  Verified
                </span>
              )}
            </div>
            {!isEditing && (
              <Button variant="ghost" size="sm" onClick={() => setIsEditing(true)}>
                <Edit2 size={16} className="mr-2" />
                Edit
              </Button>
            )}
          </div>
        </CardHeader>

        <CardBody className="space-y-6">
          {success && (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
              {success}
            </div>
          )}

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg whitespace-pre-line">
              {error}
            </div>
          )}

          <div className="space-y-4">
            <Input
              label="Username"
              type="text"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              disabled={!isEditing}
            />

            <Input
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              disabled={!isEditing}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Bio</label>
              <textarea
                className={`w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  !isEditing ? 'bg-gray-50 cursor-not-allowed' : ''
                }`}
                rows={4}
                value={formData.bio}
                onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                disabled={!isEditing}
                placeholder="Tell us about yourself and your travel experiences..."
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Travel Interests</label>
              <input
                type="text"
                className={`w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  !isEditing ? 'bg-gray-50 cursor-not-allowed' : ''
                }`}
                value={formData.interests}
                onChange={(e) => setFormData({ ...formData, interests: e.target.value })}
                disabled={!isEditing}
                placeholder="e.g., Adventure, Culture, Beach, Food, Photography"
              />
              <p className="text-xs text-gray-500 mt-1">Comma-separated values</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Dream Destinations</label>
              <input
                type="text"
                className={`w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  !isEditing ? 'bg-gray-50 cursor-not-allowed' : ''
                }`}
                value={formData.destinations}
                onChange={(e) => setFormData({ ...formData, destinations: e.target.value })}
                disabled={!isEditing}
                placeholder="e.g., Tokyo, Paris, New York, Bali, Iceland"
              />
              <p className="text-xs text-gray-500 mt-1">Comma-separated values</p>
            </div>
          </div>

          {isEditing && (
            <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
              <Button variant="secondary" onClick={handleCancel}>
                <X size={16} className="mr-2" />
                Cancel
              </Button>
              <Button onClick={handleSave} disabled={loading}>
                <Check size={16} className="mr-2" />
                {loading ? 'Saving...' : 'Save Changes'}
              </Button>
            </div>
          )}
        </CardBody>
      </Card>

      {!user?.verified && (
        <Card className="mt-6">
          <CardHeader>
            <div className="flex items-center gap-2">
              <Shield className="text-blue-600" size={20} />
              <h2 className="text-xl font-semibold text-gray-900">Verify Your Identity</h2>
            </div>
          </CardHeader>
          <CardBody>
            <p className="text-gray-600 mb-4">
              Verify your identity to build trust with other travelers and unlock additional features.
            </p>
            <Button variant="outline">
              <Shield size={16} className="mr-2" />
              Start Verification
            </Button>
          </CardBody>
        </Card>
      )}

      <Card className="mt-6">
        <CardHeader>
          <h2 className="text-xl font-semibold text-gray-900">Account Actions</h2>
        </CardHeader>
        <CardBody>
          <Button variant="danger" onClick={logout}>
            Sign Out
          </Button>
        </CardBody>
      </Card>
    </div>
  );
};
