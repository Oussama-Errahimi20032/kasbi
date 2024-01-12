import dayjs from 'dayjs';
import { IPlante } from 'app/shared/model/plante.model';
import { IParcelle } from 'app/shared/model/parcelle.model';

export interface IPlantage {
  id?: number;
  date?: dayjs.Dayjs | null;
  nombre?: number | null;
  planteLibelle?: IPlante | null;
  parcelleLibelle?: IParcelle | null;
}

export const defaultValue: Readonly<IPlantage> = {};
