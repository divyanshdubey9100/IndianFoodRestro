import { Component, forwardRef } from '@angular/core';

import {
  ControlValueAccessor,
  NG_VALUE_ACCESSOR,
} from '@angular/forms';

@Component({
  selector: 'app-date-picker',
  standalone: true,
  imports: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DatePickerComponent),
      multi: true,
    },
  ],
  templateUrl: './date-picker.component.html',
  styleUrls: ['./date-picker.component.css'],
})
export class DatePickerComponent implements ControlValueAccessor {

  value = '';
  open = false;
  disabled = false;

  selectedDate?: Date;

  month = new Date().getMonth();
  year = new Date().getFullYear();

  readonly months = [
    'January','February','March','April','May','June',
    'July','August','September','October','November','December',
  ];

  readonly years = Array.from(
    { length: this.year - 1899 },
    (_, i) => this.year - i
  );

  private onChange = (_: string) => {};
  private onTouched = () => {};

  writeValue(value: string | null): void {
    this.value = value ?? '';

    if (value) {
      const [y, m, d] = value.split('-').map(Number);
      this.selectedDate = new Date(y, m - 1, d);
      this.month = m - 1;
      this.year = y;
    } else {
      this.selectedDate = undefined;
    }
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  toggle(): void {
    if (!this.disabled) {
      this.open = !this.open;
    }
  }

  selectDay(day: number): void {
    this.setDate(new Date(this.year, this.month, day));
  }

  setToday(): void {
    const today = new Date();
    this.month = today.getMonth();
    this.year = today.getFullYear();
    this.setDate(today);
  }

  clear(): void {
    this.value = '';
    this.selectedDate = undefined;
    this.onChange('');
    this.onTouched();
    this.open = false;
  }

  private setDate(date: Date): void {
    this.selectedDate = date;

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');

    this.value = `${yyyy}-${mm}-${dd}`;
    this.onChange(this.value);
    this.onTouched();
    this.open = false;
  }

  get daysInMonth(): number[] {
    return Array.from(
      { length: new Date(this.year, this.month + 1, 0).getDate() },
      (_, i) => i + 1
    );
  }

  isSelected(day: number): boolean {
    return (
      this.selectedDate?.getDate() === day &&
      this.selectedDate?.getMonth() === this.month &&
      this.selectedDate?.getFullYear() === this.year
    );
  }
}
