export function formatNumber(value, suffix = '') {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  return `${value}${suffix}`
}

export function formatPercent(value) {
  return formatNumber(value, '%')
}
