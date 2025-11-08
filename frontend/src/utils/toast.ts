import ToastEventBus from 'primevue/toasteventbus'

export const useToastService = () => {
  const showToast = (
    severity: 'info' | 'success' | 'warn' | 'error' | 'secondary' | 'contrast',
    summary: string,
    detail: string,
    life: number,
  ) => {
    ToastEventBus.emit('add', { severity, summary, detail, life })
  }

  return { showToast }
}
