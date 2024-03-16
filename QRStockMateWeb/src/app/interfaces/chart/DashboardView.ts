import { EChartsOption } from "echarts";
import { TransportRoute } from "../transport-route";
import { Item } from "../item";
import { Warehouse } from "../warehouse";
import { Communication } from "../communication";
import { TransactionHistory } from "../transaction-history";
import { Message } from "../message";
import { Vehicle } from "../vehicle";


export function AreaChartOptions(): EChartsOption {

  return {
    title: {
      text: 'Stacked Area Chart',
      textStyle: {
        color: '#ffffff' // Cambiar el color del texto a blanco
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine'],
      textStyle: {
        color: '#ffffff' // Cambiar el color del texto a blanco
      }
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    series: [
      {
        name: 'Email',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [120, 132, 101, 134, 90, 230, 210]
      },
      {
        name: 'Union Ads',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [220, 182, 191, 234, 290, 330, 310]
      },
      {
        name: 'Video Ads',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [150, 232, 201, 154, 190, 330, 410]
      },
      {
        name: 'Direct',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [320, 332, 301, 334, 390, 330, 320]
      },
      {
        name: 'Search Engine',
        type: 'line',
        stack: 'Total',
        label: {
          show: true,
          position: 'top'
        },
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [820, 932, 901, 934, 1290, 1330, 1320]
      }
    ]
  };

}


export function LineChartOptions(): EChartsOption {
  return {
    title: {
      text: 'Temperature Change in the Coming Week',
      textStyle: {
        color: '#ffffff' // Cambiar el color del texto a blanco
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {},
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: { readOnly: false },
        magicType: { type: ['line', 'bar'] },
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value} °C'
      }
    },
    series: [
      {
        name: 'Highest',
        type: 'line',
        data: [10, 11, 13, 11, 12, 12, 9],
        animation: true, // Animación activada
        markPoint: {
          data: [
            { type: 'max', name: 'Max' },
            { type: 'min', name: 'Min' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: 'Avg' }]
        }
      },
      {
        name: 'Lowest',
        type: 'line',
        data: [1, -2, 2, 5, 3, 2, 0],
        animation: true, // Animación activada
        markPoint: {
          data: [{ name: '周最低', value: -2, xAxis: 1, yAxis: -1.5 }]
        },
        markLine: {
          data: [
            { type: 'average', name: 'Avg' },
            [
              {
                symbol: 'none',
                x: '90%',
                yAxis: 'max'
              },
              {
                symbol: 'circle',
                label: {
                  position: 'start',
                  formatter: 'Max'
                },
                type: 'max',
                name: '最高点'
              }
            ]
          ]
        }
      }
    ],
  }
}


export function BarChartOptions(): EChartsOption {
  return {
    title: {
      text: 'Bar Chart Example',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['Sales']
    },
    toolbox: {
      show: true,
      feature: {
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'Sales',
        type: 'bar',
        data: [150, 230, 224, 218, 135, 147, 260]
      }
    ]
  };
}

export function PieChartOptions(): EChartsOption {
  return {
    title: {
      text: 'Pie Chart Example',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: ['Direct', 'Email', 'Ad', 'Video', 'Search']
    },
    series: [
      {
        name: 'Access source',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 335, name: 'Direct' },
          { value: 310, name: 'Email' },
          { value: 234, name: 'Ad' },
          { value: 135, name: 'Video' },
          { value: 1548, name: 'Search' }
        ]
      }
    ]
  };
}

export function RadarChartOptions(): EChartsOption {
  return {
    title: {
      text: 'Radar Chart Example',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {},
    legend: {
      data: ['Allocated Budget', 'Actual Spending']
    },
    radar: {
      // Indicadores (ejes) del radar
      indicator: [
        { name: 'Sales', max: 6500 },
        { name: 'Administration', max: 16000 },
        { name: 'Information Technology', max: 30000 },
        { name: 'Customer Support', max: 38000 },
        { name: 'Development', max: 52000 },
        { name: 'Marketing', max: 25000 }
      ]
    },
    series: [
      {
        name: 'Budget vs spending',
        type: 'radar',
        lineStyle: { // Aquí especificamos el estilo de la línea
          color: 'rgb(255, 0, 0)' // Cambia el color de la línea a rojo (formato RGB)
        },
        data: [
          {
            value: [4300, 10000, 28000, 35000, 50000, 19000],
            name: 'Allocated Budget'
          },
          {
            value: [5000, 14000, 28000, 31000, 42000, 21000],
            name: 'Actual Spending'
          }
        ]
      }
    ]
  };
}

//CUSTOMIZABLE

export function RadarChart_Routes(transportRoutes: TransportRoute[]): EChartsOption {
  const colors: string[] = [
    'rgb(0, 0, 255)',      // Azul
    'rgb(255, 255, 0)',    // Amarillo
    'rgb(128, 0, 128)',    // Púrpura
    'rgb(255, 0, 0)',      // Rojo
    'rgb(128, 128, 128)',  // Gris
    'rgb(255, 165, 0)',    // Naranja
    'rgb(165, 42, 42)',    // Marrón
    'rgb(255, 0, 255)',    // Magenta
    'rgb(0, 255, 255)',    // Cian
    'rgb(0, 128, 0)'       // Verde
  ];
  
  const routeNames: string[] = [];
  const routeValues: number[][] = [];

  // Recorre las rutas de transporte para extraer sus características
  transportRoutes.forEach(route => {
    routeNames.push(route.code); // Añade el nombre de la ruta
    routeValues.push([
      route.palets.split(';').length, // Ejemplo de característica: número de palets
      route.assignedVehicleId, // Ejemplo de característica: ID del vehículo asignado
      route.carrierId, // Ejemplo de característica: ID del conductor
      // Agrega más características según sea necesario
    ]);
  });

  return {
    title: {
      text: 'Radar Chart ',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {},
    legend: {
      data: ['Route Characteristics']
    },
    radar: {
      indicator: [
        { name: 'Number of Palets', max: getMaxValue(routeValues, 0) },
        { name: 'Assigned Vehicle ID', max: getMaxValue(routeValues, 1) },
        { name: 'Carrier ID', max: getMaxValue(routeValues, 2) },
        // Añade más indicadores según las características que desees mostrar
      ]
    },
    series: [
      {
        name: 'Route Characteristics',
        type: 'radar',
        data: routeValues.map((values, index) => ({
          value: values,
          name: routeNames[index],
          itemStyle: {
            color: colors[index % colors.length]
          },
        }))
      }
    ]
  };
}

// Función para obtener el valor máximo de una característica en todas las rutas
function getMaxValue(routeValues: number[][], index: number): number {
  return Math.max(...routeValues.map(values => values[index]));
}


export function BarChart_Item(items: Item[]): EChartsOption {
  // Utiliza los datos de la entidad Item para generar el gráfico de barras
  // Por ejemplo, puedes mostrar la cantidad de existencias de diferentes artículos en un almacén

  // Extrae los nombres de los artículos y sus cantidades de existencias
  const itemNames: string[] = [];
  const stockQuantities: number[] = [];

  items.forEach(item => {
    itemNames.push(item.name); // Agrega el nombre del artículo
    stockQuantities.push(item.stock); // Agrega la cantidad de existencias
  });

  return {
    title: {
      text: 'Stock Quantity by Item',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {},
    xAxis: {
      type: 'category',
      data: itemNames
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'Stock Quantity',
        type: 'bar',
        itemStyle: { // Aquí especificamos el estilo de la barra
          color: 'rgb(0, 128, 0)' // Cambia el color de la barra a rojo (formato RGB)
        },
        data: stockQuantities
      }
    ]
  };
}

export function AreaChart_PerWarehouse(warehouses: Warehouse[], items: Item[], transportRoutes: TransportRoute[]): EChartsOption {
  const warehouseNames: string[] = [];
  const itemCountsPerWarehouse: number[] = [];
  const stockCountsPerWarehouse: number[] = [];
  const routeOutputsPerWarehouse: number[] = [];
  const routeInputsPerWarehouse: number[] = [];

  // Crear un mapa para almacenar las cantidades de ítems, stock, rutas de salida y rutas de entrada por almacén
  const itemsPerWarehouse = new Map<number, number>();
  const stockPerWarehouse = new Map<number, number>();
  const routeOutputsPerWarehouseMap = new Map<number, number>();
  const routeInputsPerWarehouseMap = new Map<number, number>();

  // Calcular la cantidad total de ítems, stock, rutas de salida y rutas de entrada por almacén
  items.forEach(item => {
    const warehouseId = item.warehouseId;
    const stock = item.stock;

    if (itemsPerWarehouse.has(warehouseId)) {
      itemsPerWarehouse.set(warehouseId, itemsPerWarehouse.get(warehouseId)! + 1);
    } else {
      itemsPerWarehouse.set(warehouseId, 1);
    }

    if (stockPerWarehouse.has(warehouseId)) {
      stockPerWarehouse.set(warehouseId, stockPerWarehouse.get(warehouseId)! + stock);
    } else {
      stockPerWarehouse.set(warehouseId, stock);
    }
  });

  transportRoutes.forEach(route => {
    const outputWarehouseId = route.startLocation;
    const inputWarehouseId = route.endLocation;

    if (routeOutputsPerWarehouseMap.has(parseInt(outputWarehouseId))) {
      routeOutputsPerWarehouseMap.set(parseInt(outputWarehouseId), routeOutputsPerWarehouseMap.get(parseInt(outputWarehouseId))! + 1);
    } else {
      routeOutputsPerWarehouseMap.set(parseInt(outputWarehouseId), 1);
    }

    if (routeInputsPerWarehouseMap.has(parseInt(inputWarehouseId))) {
      routeInputsPerWarehouseMap.set(parseInt(inputWarehouseId), routeInputsPerWarehouseMap.get(parseInt(inputWarehouseId))! + 1);
    } else {
      routeInputsPerWarehouseMap.set(parseInt(inputWarehouseId), 1);
    }
  });

  // Iterar sobre los almacenes para obtener los nombres, cantidades de ítems, stock, rutas de salida y rutas de entrada
  warehouses.forEach(warehouse => {
    const warehouseId = warehouse.id;
    const itemCount = itemsPerWarehouse.get(warehouseId) || 0;
    const stockCount = stockPerWarehouse.get(warehouseId) || 0;
    const routeOutputCount = routeOutputsPerWarehouseMap.get(warehouseId) || 0;
    const routeInputCount = routeInputsPerWarehouseMap.get(warehouseId) || 0;

    warehouseNames.push(warehouse.name);
    itemCountsPerWarehouse.push(itemCount);
    stockCountsPerWarehouse.push(stockCount);
    routeOutputsPerWarehouse.push(routeOutputCount);
    routeInputsPerWarehouse.push(routeInputCount);
  });

  return {
    title: {
      text: 'Entitites per Warehouse',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['Item Count', 'Stock Count', 'Route Outputs', 'Route Inputs'],
      textStyle: {
        color: '#ffffff'
      }
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: false,
        data: warehouseNames,
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    series: [
      {
        name: 'Item Count',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: itemCountsPerWarehouse
      },
      {
        name: 'Stock Count',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: stockCountsPerWarehouse
      },
      {
        name: 'Route Outputs',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: routeOutputsPerWarehouse
      },
      {
        name: 'Route Inputs',
        type: 'line',
        stack: 'Total',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: routeInputsPerWarehouse
      }
    ]
  };
}

export function LineChart_POOR(data: (Communication | TransactionHistory | Message)[]): EChartsOption {
  const xAxisData: string[] = [];
  const seriesData: number[] = [];
  let dataType: string = '';

  // Crear un mapa para almacenar el recuento de comunicaciones o transacciones por fecha
  const countByDate = new Map<string, number>();

  data.forEach(item => {
    let date: Date;

    if ('sentDate' in item && !('senderContactId' in item)) {
      date = new Date(item.sentDate);
      dataType = 'Communication';
    } else if ('created' in item) {
      date = new Date(item.created);
      dataType = 'TransactionHistory';
    } else if ('content' in item && 'senderContactId' in item) {
      date = new Date(item.sentDate);
      dataType = 'Message';
    } else {
      throw new Error('No se puede determinar la fecha');
    }

    const formattedDate = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;

    // Incrementar el recuento de comunicaciones o transacciones para esta fecha
    countByDate.set(formattedDate, (countByDate.get(formattedDate) || 0) + 1);
  });

  // Convertir el mapa a un arreglo de objetos { fecha, cantidad }
  countByDate.forEach((count, date) => {
    xAxisData.push(date);
    seriesData.push(count);
  });

  return {
    title: {
      text: 'Trend Over Time : ' + dataType,
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {},
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: { readOnly: false },
        magicType: { type: ['line', 'bar'] },
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'Trend type ' + dataType,
        type: 'line',
        data: seriesData,
        animation: true,
        markPoint: {
          data: [
            { type: 'max', name: 'Max' },
            { type: 'min', name: 'Min' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: 'Avg' }]
        }
      }
    ]
  };
}


export function LineChart_ALL(
  communications: Communication[],
  transactions: TransactionHistory[],
  messages: Message[]
): EChartsOption {
  const xAxisData: string[] = [];
  const seriesDataCommunication: number[] = [];
  const seriesDataTransaction: number[] = [];
  const seriesDataMessage: number[] = [];

  // Crear un mapa para almacenar el recuento de elementos por fecha para cada tipo de datos
  const countByDateCommunication = new Map<string, number>();
  const countByDateTransaction = new Map<string, number>();
  const countByDateMessage = new Map<string, number>();

  // Procesar los datos de Communication
  communications.forEach(item => {
    const date = new Date(item.sentDate);
    const formattedDate = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    countByDateCommunication.set(formattedDate, (countByDateCommunication.get(formattedDate) || 0) + 1);
  });

  // Procesar los datos de TransactionHistory
  transactions.forEach(item => {
    const date = new Date(item.created);
    const formattedDate = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    countByDateTransaction.set(formattedDate, (countByDateTransaction.get(formattedDate) || 0) + 1);
  });

  // Procesar los datos de Message
  messages.forEach(item => {
    const date = new Date(item.sentDate);
    const formattedDate = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    countByDateMessage.set(formattedDate, (countByDateMessage.get(formattedDate) || 0) + 1);
  });

  // Obtener todas las fechas únicas
  const allDatesSet = new Set([...countByDateCommunication.keys(), ...countByDateTransaction.keys(), ...countByDateMessage.keys()]);
  const allDates = Array.from(allDatesSet);

  // Ordenar las fechas
  allDates.sort((a, b) => new Date(a).getTime() - new Date(b).getTime());

  // Generar los datos para el eje X y las series para cada tipo de datos
  allDates.forEach(date => {
    xAxisData.push(date);

    seriesDataCommunication.push(countByDateCommunication.get(date) || 0);
    seriesDataTransaction.push(countByDateTransaction.get(date) || 0);
    seriesDataMessage.push(countByDateMessage.get(date) || 0);
  });

  return {
    title: {
      text: 'Trend Over Time for All Types',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['Communication', 'Transaction', 'Message']
    },
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: { readOnly: false },
        magicType: { type: ['line', 'bar'] },
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'Communication',
        type: 'line',
        data: seriesDataCommunication,
        animation: true,
        markPoint: {
          data: [
            { type: 'max', name: 'Max' },
            { type: 'min', name: 'Min' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: 'Avg' }]
        }
      },
      {
        name: 'Transaction',
        type: 'line',
        data: seriesDataTransaction,
        animation: true,
        markPoint: {
          data: [
            { type: 'max', name: 'Max' },
            { type: 'min', name: 'Min' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: 'Avg' }]
        }
      },
      {
        name: 'Message',
        type: 'line',
        data: seriesDataMessage,
        animation: true,
        markPoint: {
          data: [
            { type: 'max', name: 'Max' },
            { type: 'min', name: 'Min' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: 'Avg' }]
        }
      }
    ]
  };
}
export function PieChart_TransportRoute_End(routes: TransportRoute[], warehouses: Warehouse[]): EChartsOption {
  const colors: string[] = [
    'rgb(255, 0, 0)',      // Rojo
    'rgb(0, 128, 0)',      // Verde
    'rgb(0, 0, 255)',      // Azul
    'rgb(255, 255, 0)',    // Amarillo
    'rgb(255, 165, 0)',    // Naranja
    'rgb(128, 0, 128)',    // Púrpura
    'rgb(0, 255, 255)',    // Cian
    'rgb(255, 0, 255)',    // Magenta
    'rgb(128, 128, 128)',  // Gris
    'rgb(165, 42, 42)'     // Marrón
  ];  // Creamos un mapa para mapear el ID del almacén con su nombre
  const warehouseMap = new Map<number, string>();
  warehouses.forEach(warehouse => {
    warehouseMap.set(warehouse.id, warehouse.name);
  });

  // Creamos un mapa para contar la cantidad de rutas por destino
  const routesByDestination = new Map<string, number>();
  routes.forEach(route => {
    const destination = warehouseMap.get(parseInt(route.endLocation)) || 'Unknown'; // Usamos 'Unknown' si no se encuentra el nombre del almacén
    routesByDestination.set(destination, (routesByDestination.get(destination) || 0) + 1);
  });

  // Preparamos los datos para el gráfico de pastel
  const data: { value: number; name: string; itemStyle: { color: string } }[] = [];
  let colorIndex = 0; // Índice para seleccionar colores de la lista proporcionada
  routesByDestination.forEach((count, destination) => {
    // Seleccionar un color de la lista de colores
    const color = colors[colorIndex % colors.length];
    data.push({ value: count, name: destination, itemStyle: { color: color } });
    colorIndex++;
  });

  // Configuración del gráfico de pastel
  return {
    title: {
      text: 'Number of Destination Routes per Warehouse',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: Array.from(routesByDestination.keys())
    },
    series: [
      {
        name: 'Cantidad de Rutas',
        type: 'pie',
        radius: '50%',
        data: data
      }
    ]
  };
}



export function PieChart_TransportRoute_Start(routes: TransportRoute[], warehouses: Warehouse[]): EChartsOption {
  // Creamos un mapa para mapear el ID del almacén con su nombre
  const colors: string[] = [
    'rgb(0, 128, 0)',      // Verde
    'rgb(128, 0, 128)',    // Púrpura
    'rgb(255, 255, 0)',    // Amarillo
    'rgb(255, 0, 0)',      // Rojo
    'rgb(0, 0, 255)',      // Azul
    'rgb(128, 128, 128)',  // Gris
    'rgb(255, 165, 0)',    // Naranja
    'rgb(255, 0, 255)',    // Magenta
    'rgb(165, 42, 42)',    // Marrón
    'rgb(0, 255, 255)'     // Cian
  ];

  
  const warehouseMap = new Map<number, string>();
  warehouses.forEach(warehouse => {
    warehouseMap.set(warehouse.id, warehouse.name);
  });

  // Creamos un mapa para contar la cantidad de rutas por almacén de salida
  const routesByDeparture = new Map<string, number>();
  routes.forEach(route => {
    const departure = warehouseMap.get(parseInt(route.startLocation)) || 'Unknown'; // Usamos 'Unknown' si no se encuentra el nombre del almacén
    routesByDeparture.set(departure, (routesByDeparture.get(departure) || 0) + 1);
  });

  // Preparamos los datos para el gráfico de pastel
  const data: { value: number; name: string; itemStyle: { color: string } }[] = [];
  let colorIndex = 0; // Índice para seleccionar colores de la lista proporcionada
  routesByDeparture.forEach((count, departure) => {
    data.push({ value: count, name: departure, itemStyle: { color: colors[data.length % colors.length] } });
  });
  
  // Configuración del gráfico de pastel
  return {
    title: {
      text: 'Number of Departure Routes per Warehouse',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: Array.from(routesByDeparture.keys())
    },
    series: [
      {
        name: 'Cantidad de Rutas',
        type: 'pie',
        radius: '50%',
        data: data
      }
    ]
  };
}



export function BarChart_RoutesPerVehicle(vehicles: Vehicle[], routes: TransportRoute[]): EChartsOption {
  const routesPerVehicle: { [vehicleId: number]: number } = {};

  // Contar la cantidad de rutas por vehículo
  routes.forEach(route => {
    routesPerVehicle[route.assignedVehicleId] = (routesPerVehicle[route.assignedVehicleId] || 0) + 1;
  });

  // Crear los datos para el gráfico de barras
  const xAxisData: string[] = vehicles.map(vehicle => vehicle.licensePlate);
  const seriesData: number[] = vehicles.map(vehicle => routesPerVehicle[vehicle.id] || 0);

  return {
    title: {
      text: 'Number of Routes per Vehicle',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: seriesData,
      itemStyle: { // Aquí especificamos el estilo de la barra
        color: 'rgb(255, 165, 0)' // Cambia el color de la barra a rojo (formato RGB)
      },
      type: 'bar'
    }]
  };
}


export function PieChartVehicleManufacturer(vehicles: Vehicle[]): EChartsOption {
  const manufacturerCount: { [manufacturer: string]: number } = {};

  // Contar la cantidad de vehículos por fabricante
  vehicles.forEach(vehicle => {
    manufacturerCount[vehicle.make] = (manufacturerCount[vehicle.make] || 0) + 1;
  });

  // Crear los datos para el gráfico de pastel
  const legendData: string[] = Object.keys(manufacturerCount);
  const seriesData = legendData.map(manufacturer => ({
    name: manufacturer,
    value: manufacturerCount[manufacturer]
  }));

  return {
    title: {
      text: 'Vehicle Distribution by Manufacturer',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: legendData
    },
    series: [{
      name: 'Vehículos',
      type: 'pie',
      radius: '50%',
      data: seriesData
    }]
  };
}


export function BubbleChartMaxLoadByModel(vehicles: Vehicle[]): EChartsOption {
  const colors: string[] = [
    'rgb(255, 165, 0)',    // Naranja
    'rgb(0, 0, 255)',      // Azul
    'rgb(255, 0, 0)',      // Rojo
    'rgb(0, 128, 0)',      // Verde
    'rgb(255, 255, 0)',    // Amarillo
    'rgb(128, 128, 128)',  // Gris
    'rgb(165, 42, 42)',    // Marrón
    'rgb(255, 0, 255)',    // Magenta
    'rgb(0, 255, 255)',    // Cian
    'rgb(128, 0, 128)'     // Púrpura
  ];
  
  return {
    title: {
      text: 'Relationship between Maximum Load and Vehicle Model',
      textStyle: {
        color: '#ffffff'
      }
    },
    xAxis: {
      type: 'category',
      data: vehicles.map(vehicle => vehicle.model)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: vehicles.map(vehicle => [vehicle.model, vehicle.maxLoad]),
      type: 'scatter',
      itemStyle: {
        color: function (params: any) {
          return colors[params.dataIndex % colors.length];
        }
      }
    }]
  };
}
export function LineChartRoutesByDate(routes: TransportRoute[]): EChartsOption {
  // Creamos un mapa para contar la cantidad de rutas por fecha
  const routeCountByDate = new Map<string, number>();

  // Iteramos sobre las rutas para contar la cantidad de rutas por fecha
  routes.forEach(route => {
    const dateKey = route.date.toString().slice(0, 10); // Convertimos la fecha a formato de cadena YYYY-MM-DD

    if (routeCountByDate.has(dateKey)) {
      routeCountByDate.set(dateKey, routeCountByDate.get(dateKey)! + 1); // Incrementamos el conteo de rutas para esta fecha
    } else {
      routeCountByDate.set(dateKey, 1); // Inicializamos el conteo de rutas para esta fecha
    }
  });

  // Obtenemos las fechas y la cantidad de rutas para cada fecha
  const dates = Array.from(routeCountByDate.keys());
  const routeCounts = Array.from(routeCountByDate.values());

  return {
    title: {
      text: 'Number of Transportation Routes by Date',
      textStyle: {
        color: '#ffffff'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {},
    toolbox: {
      show: true,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        dataView: { readOnly: false },
        magicType: { type: ['line', 'bar'] },
        restore: {},
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      name: 'Fecha'
    },
    yAxis: {
      type: 'value',
      name: 'Cantidad de Rutas'
    },
    series: [
      {
        name: 'Cantidad de Rutas',
        type: 'line',
        data: routeCounts,
        animation: true,
        lineStyle: { // Aquí especificamos el estilo de la línea
          color: 'rgb(255, 0, 0)' // Cambia el color de la línea a rojo (formato RGB)
        },
        markPoint: {
          data: [
            { type: 'max', name: 'Máximo' },
            { type: 'min', name: 'Mínimo' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: 'Promedio' }]
        }
      }
    ]
  };
}