import { AlertCircle, Server } from 'lucide-react';

export const BackendNotice = () => {
  return (
    <div className="fixed bottom-4 right-4 max-w-md bg-yellow-50 border-2 border-yellow-400 rounded-lg shadow-lg p-4 z-50">
      <div className="flex items-start gap-3">
        <div className="flex-shrink-0">
          <Server className="text-yellow-600" size={24} />
        </div>
        <div>
          <div className="flex items-center gap-2 mb-2">
            <AlertCircle className="text-yellow-600" size={16} />
            <h3 className="font-semibold text-yellow-900">Backend Not Running</h3>
          </div>
          <p className="text-sm text-yellow-800 mb-3">
            This is a frontend preview. To use the full application with authentication and data persistence:
          </p>
          <div className="bg-yellow-100 rounded p-3 text-sm text-yellow-900 font-mono">
            <div className="mb-1">1. Open terminal</div>
            <div className="mb-1">2. cd backend/travelBuddy</div>
            <div className="mb-1">3. ./mvnw spring-boot:run</div>
          </div>
          <p className="text-xs text-yellow-700 mt-3">
            See <span className="font-semibold">RUN_APPLICATION.md</span> for complete setup instructions.
          </p>
        </div>
      </div>
    </div>
  );
};
