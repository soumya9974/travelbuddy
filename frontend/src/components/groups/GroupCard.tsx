import { TravelGroup } from '../../types';
import { Card, CardBody } from '../ui/Card';
import { MapPin, Calendar, Users } from 'lucide-react';

interface GroupCardProps {
  group: TravelGroup;
  onClick: () => void;
}

export const GroupCard = ({ group, onClick }: GroupCardProps) => {
  const formatDate = (date: string) => {
    return new Date(date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  const gradients = [
    'from-blue-500 via-cyan-500 to-teal-500',
    'from-rose-500 via-orange-500 to-amber-500',
    'from-green-500 via-emerald-500 to-teal-500',
    'from-cyan-500 via-blue-500 to-sky-500',
    'from-amber-500 via-yellow-500 to-orange-500',
    'from-teal-500 via-green-500 to-emerald-500',
  ];

  const gradientClass = gradients[group.id % gradients.length];

  return (
    <Card hover className="cursor-pointer group" onClick={onClick}>
      <div className={`h-40 bg-gradient-to-br ${gradientClass} relative overflow-hidden transition-transform duration-300 group-hover:scale-105`}>
        <div className="absolute inset-0 bg-black/10"></div>
      </div>
      <CardBody>
        <h3 className="text-xl font-bold text-gray-900 mb-2">{group.name}</h3>
        <p className="text-gray-600 text-sm mb-4 line-clamp-2 leading-relaxed">{group.description}</p>

        <div className="space-y-2">
          <div className="flex items-center text-sm text-gray-700">
            <MapPin size={16} className="mr-2 text-blue-600" />
            {group.destination}
          </div>

          <div className="flex items-center text-sm text-gray-700">
            <Calendar size={16} className="mr-2 text-blue-600" />
            {formatDate(group.startDate)} - {formatDate(group.endDate)}
          </div>

          {group.memberCount !== undefined && (
            <div className="flex items-center text-sm text-gray-700">
              <Users size={16} className="mr-2 text-blue-600" />
              {group.memberCount} {group.memberCount === 1 ? 'member' : 'members'}
            </div>
          )}
        </div>
      </CardBody>
    </Card>
  );
};
