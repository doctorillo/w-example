import { PropertyCardUI } from '../bookings/PropertyCardUI'

export interface PropertyPoint {
  id: string;
  customerId: string;
  supplierId: string;
  name: string;
  star: number;
  price: number;
  latitude: number;
  longitude: number;
  selected: boolean;
}

export function toPoint (x: PropertyCardUI, customerId: string, selected = false, lat = 49.8037633, lng = 15.4749126): PropertyPoint {
  const price = x.bestPrice ? Math.round(x.bestPrice.total.value) : 0
  const latitude = x.location ? x.location.lat : lat
  const longitude = x.location ? x.location.lng : lng
  return {
    id: x.id,
    supplierId: x.supplierId,
    customerId: customerId,
    name: x.name,
    star: x.star,
    price,
    latitude,
    longitude,
    selected,
  }
}