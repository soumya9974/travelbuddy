import { useState, FormEvent } from 'react';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Card, CardBody } from '../components/ui/Card';
import { Plane, ArrowLeft, Mail } from 'lucide-react';
import { API_BASE_URL } from '../config/api';

interface ForgotPasswordProps {
  onBackToLogin: () => void;
}

export const ForgotPassword = ({ onBackToLogin }: ForgotPasswordProps) => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await fetch(`${API_BASE_URL}/auth/forgot-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Failed to send reset email' }));
        throw new Error(error.error || 'Failed to send reset email');
      }

      setSuccess(true);
    } catch (error) {
      if (error instanceof TypeError && error.message.includes('fetch')) {
        setError('Backend server is not running. Please start the backend first.\n\nTo run locally:\n1. Open terminal\n2. cd backend/travelBuddy\n3. ./mvnw spring-boot:run\n\nSee RUN_APPLICATION.md for details.');
      } else {
        setError(error instanceof Error ? error.message : 'Failed to send reset email');
      }
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen relative flex items-center justify-center p-4">
        <div
          className="absolute inset-0 bg-cover bg-center bg-no-repeat"
          style={{
            backgroundImage: "url('https://images.pexels.com/photos/1285625/pexels-photo-1285625.jpeg?auto=compress&cs=tinysrgb&w=1920')",
          }}
        >
          <div className="absolute inset-0 bg-gradient-to-br from-blue-900/80 via-cyan-900/75 to-teal-900/80" />
        </div>

        <Card className="w-full max-w-md relative z-10 backdrop-blur-sm bg-white/95">
          <CardBody className="p-8">
            <div className="flex justify-center mb-6">
              <div className="bg-gradient-to-r from-green-600 to-emerald-600 p-3 rounded-full shadow-lg">
                <Mail className="text-white" size={32} />
              </div>
            </div>

            <h1 className="text-3xl md:text-4xl font-bold text-center text-gray-900 mb-2">
              Check Your Email
            </h1>
            <p className="text-center text-gray-600 text-lg mb-6">
              We've sent a password reset link to <strong className="text-gray-900">{email}</strong>
            </p>

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
              <p className="text-sm text-blue-800">
                Click the link in the email to reset your password. The link will expire in 24 hours.
              </p>
            </div>

            <Button onClick={onBackToLogin} variant="outline" className="w-full">
              <ArrowLeft size={16} className="mr-2" />
              Back to Login
            </Button>

            <div className="mt-4 text-center">
              <p className="text-sm text-gray-600">
                Didn't receive the email?{' '}
                <button
                  onClick={() => setSuccess(false)}
                  className="text-blue-600 hover:text-blue-700 font-medium"
                >
                  Try again
                </button>
              </p>
            </div>
          </CardBody>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen relative flex items-center justify-center p-4">
      <div
        className="absolute inset-0 bg-cover bg-center bg-no-repeat"
        style={{
          backgroundImage: "url('https://images.pexels.com/photos/1285625/pexels-photo-1285625.jpeg?auto=compress&cs=tinysrgb&w=1920')",
        }}
      >
        <div className="absolute inset-0 bg-gradient-to-br from-blue-900/80 via-cyan-900/75 to-teal-900/80" />
      </div>

      <Card className="w-full max-w-md relative z-10 backdrop-blur-sm bg-white/95">
        <CardBody className="p-8">
          <div className="flex justify-center mb-6">
            <div className="bg-gradient-to-r from-blue-600 to-cyan-600 p-3 rounded-full shadow-lg">
              <Plane className="text-white" size={32} />
            </div>
          </div>

          <h1 className="text-3xl md:text-4xl font-bold text-center text-gray-900 mb-2">
            Forgot Password?
          </h1>
          <p className="text-center text-gray-600 text-lg mb-8">
            No worries! Enter your email and we'll send you a reset link
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Email Address"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              autoComplete="email"
              required
            />

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm whitespace-pre-line">
                {error}
              </div>
            )}

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Sending...' : 'Send Reset Link'}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <button
              onClick={onBackToLogin}
              className="text-gray-600 hover:text-gray-700 font-medium inline-flex items-center"
            >
              <ArrowLeft size={16} className="mr-1" />
              Back to Login
            </button>
          </div>
        </CardBody>
      </Card>
    </div>
  );
};
