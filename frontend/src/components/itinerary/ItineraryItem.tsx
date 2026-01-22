import { Itinerary } from '../../types';
import { MapPin, Clock, Trash2 } from 'lucide-react';
import { Button } from '../ui/Button';

interface ItineraryItemProps {
  item: Itinerary;
  onDelete: () => void;
}

export const ItineraryItem = ({ item, onDelete }: ItineraryItemProps) => {
  const formatTime = (time?: string) => {
    if (!time) return '';
    return new Date(`2000-01-01T${time}`).toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
    });
  };

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-lg transition-all duration-300 hover:border-gray-300">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h5 className="font-bold text-gray-900 mb-2 text-lg">{item.title}</h5>
          <p className="text-sm text-gray-600 mb-3 leading-relaxed">{item.description}</p>

          <div className="flex flex-wrap gap-4">
            <div className="flex items-center text-sm text-gray-700">
              <MapPin size={16} className="mr-2 text-blue-600" />
              {item.location}
            </div>

            {(item.startTime || item.endTime) && (
              <div className="flex items-center text-sm text-gray-700">
                <Clock size={16} className="mr-2 text-blue-600" />
                {item.startTime && formatTime(item.startTime)}
                {item.startTime && item.endTime && ' - '}
                {item.endTime && formatTime(item.endTime)}
              </div>
            )}
          </div>
        </div>

        <Button variant="ghost" size="sm" onClick={onDelete}>
          <Trash2 size={16} className="text-red-600" />
        </Button>
      </div>
    </div>
  );
};
