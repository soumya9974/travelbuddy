import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Itinerary } from '../../types';
import { apiRequest } from '../../utils/api';
import { Button } from '../ui/Button';
import { Spinner } from '../ui/Spinner';
import { Plus, MapPin, Clock } from 'lucide-react';
import { CreateItineraryModal } from './CreateItineraryModal';
import { ItineraryItem } from './ItineraryItem';

interface ItineraryListProps {
  groupId: number;
}

export const ItineraryList = ({ groupId }: ItineraryListProps) => {
  const { token } = useAuth();
  const [itineraries, setItineraries] = useState<Itinerary[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);

  const fetchItineraries = async () => {
    try {
      const data = await apiRequest(`/groups/${groupId}/itineraries`, {
        token: token!,
      });
      setItineraries(data.sort((a: Itinerary, b: Itinerary) => a.day - b.day));
    } catch (err) {
      console.error('Failed to fetch itineraries:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItineraries();
  }, [groupId]);

  const handleCreateItinerary = () => {
    setShowCreateModal(false);
    fetchItineraries();
  };

  const handleDeleteItinerary = async (id: number) => {
    try {
      await apiRequest(`/itineraries/${id}`, {
        method: 'DELETE',
        token: token!,
      });
      fetchItineraries();
    } catch (err) {
      console.error('Failed to delete itinerary:', err);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Spinner />
      </div>
    );
  }

  const groupedByDay = itineraries.reduce((acc, item) => {
    if (!acc[item.day]) {
      acc[item.day] = [];
    }
    acc[item.day].push(item);
    return acc;
  }, {} as Record<number, Itinerary[]>);

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-xl font-bold text-gray-900">Trip Itinerary</h3>
        <Button size="sm" onClick={() => setShowCreateModal(true)}>
          <Plus size={16} className="mr-2" />
          Add Activity
        </Button>
      </div>

      {itineraries.length === 0 ? (
        <div className="text-center py-12">
          <MapPin size={48} className="mx-auto text-gray-400 mb-4" />
          <h3 className="text-xl font-bold text-gray-900 mb-2">No itinerary yet</h3>
          <p className="text-gray-600 mb-4 text-lg">Start planning your trip activities!</p>
          <Button onClick={() => setShowCreateModal(true)}>
            <Plus size={20} className="mr-2" />
            Add First Activity
          </Button>
        </div>
      ) : (
        <div className="space-y-6">
          {Object.entries(groupedByDay).map(([day, items]) => (
            <div key={day}>
              <h4 className="font-bold text-gray-900 mb-3 flex items-center">
                <span className="bg-blue-100 text-blue-800 px-4 py-2 rounded-full text-sm font-semibold shadow-sm">
                  Day {day}
                </span>
              </h4>
              <div className="space-y-3 ml-4">
                {items.map((item) => (
                  <ItineraryItem
                    key={item.id}
                    item={item}
                    onDelete={() => handleDeleteItinerary(item.id)}
                  />
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      <CreateItineraryModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSuccess={handleCreateItinerary}
        groupId={groupId}
      />
    </div>
  );
};
